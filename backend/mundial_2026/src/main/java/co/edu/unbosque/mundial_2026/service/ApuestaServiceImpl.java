package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;
import co.edu.unbosque.mundial_2026.entity.Apuesta;
import co.edu.unbosque.mundial_2026.entity.Participacion;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.entity.Pronostico;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.ApuestaCerradaException;
import co.edu.unbosque.mundial_2026.exception.ApuestaNotFoundException;
import co.edu.unbosque.mundial_2026.exception.CodigoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.EstadoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.ParticipacionNotFoundException;
import co.edu.unbosque.mundial_2026.exception.PartidoYaIniciadoException;
import co.edu.unbosque.mundial_2026.exception.PronosticoNotFoundException;
import co.edu.unbosque.mundial_2026.exception.UsuarioYaEnApuestaException;
import co.edu.unbosque.mundial_2026.repository.ApuestaRepository;
import co.edu.unbosque.mundial_2026.repository.ParticipacionRepository;
import co.edu.unbosque.mundial_2026.repository.PronosticoRepository;

/**
 * Implementación del servicio encargado de gestionar toda la lógica de negocio
 * relacionada con las pollas (apuestas) del Mundial 2026.
 * Cubre el ciclo de vida completo de una polla: creación, unión de participantes,
 * registro y edición de pronósticos, cierre, cálculo de puntos y generación del ranking.
 * Cada operación relevante queda registrada en el sistema de auditoría
 * y se envían notificaciones a los usuarios cuando corresponde
 */
@Service
public class ApuestaServiceImpl implements ApuestaService {

        private final ApuestaRepository apuestaRepository;
        private final PronosticoRepository pronosticoRepository;
        private final ParticipacionRepository participacionRepository;
        private final UsuarioService usuarioService;
        private static final String ESTADO_ABIERTA = "ABIERTA";
        private static final String ESTADO_CERRADA = "CERRADA";
        private static final String APUESTA_NO_ENCONTRADA = "Apuesta no encontrada";
        private static final String PRONOSTICO_NO_ENCONTRADO = "Pronostico no encontrado";
        private final EventoAuditoriaService auditoriaService;
        private static final String ENTIDAD_APUESTA = "Apuesta";
        private static final String ENTIDAD_PRONOSTICO = "Pronostico";
        private final PartidoService partidoService;
        private final NotificacionService notificacionService;
        private static final String PARTICIPACION_NO_ENCONTRADA = "Participacion no encontrada";
        private static final String VS_SEPARATOR = " vs ";
        private static final String PIPE_SEPARATOR = " | ";

        public ApuestaServiceImpl(ApuestaRepository apuestaRepository,
                        PronosticoRepository pronosticoRepository,
                        ParticipacionRepository participacionRepository,
                        UsuarioService usuarioService,
                        PartidoService partidoService,
                        NotificacionService notificacionService,
                        EventoAuditoriaService auditoriaService) {
                this.apuestaRepository = apuestaRepository;
                this.pronosticoRepository = pronosticoRepository;
                this.participacionRepository = participacionRepository;
                this.usuarioService = usuarioService;
                this.partidoService = partidoService;
                this.notificacionService = notificacionService;
                this.auditoriaService = auditoriaService;
        }

        /**
         * Crea una nueva polla con estado ABIERTA y genera automáticamente un código
         * de invitación único. El usuario que la crea queda registrado como participante
         * con 0 puntos y la operación se guarda en auditoría
         *
         * @param dto datos de la polla a crear, incluyendo nombre, fecha de cierre e id del usuario creador
         * @return {@link ApuestaDTO} con la información de la polla recién creada
         * @throws ApuestaCerradaException si la fecha de cierre proporcionada ya pasó
         */
        @Transactional
        @Override
        public ApuestaDTO crearApuesta(ApuestaRequestDTO dto) {
                final Usuario usuario = usuarioService.obtenerEntidadPorId(dto.getUsuarioId());
                if (dto.getFechaCierre() != null && dto.getFechaCierre().isBefore(LocalDateTime.now())) {
                        throw new ApuestaCerradaException("La fecha de cierre debe ser futura");
                }
                final Apuesta apuesta = new Apuesta();
                apuesta.setNombre(dto.getNombre());
                apuesta.setFechaCierre(dto.getFechaCierre());
                apuesta.setCreadaPor(usuario);
                apuesta.setEstado(ESTADO_ABIERTA);
                apuesta.setCodigoInvitacion(UUID.randomUUID().toString());
                apuestaRepository.save(apuesta);

                final Participacion partCreador = new Participacion();
                partCreador.setUsuario(usuario);
                partCreador.setApuesta(apuesta);
                partCreador.setPuntos(0);
                participacionRepository.save(partCreador);

                auditoriaService.registrar(
                                "APUESTA_CREADA",
                                usuario.getNombre() + " " + usuario.getApellido()
                                                + " creó la polla '" + apuesta.getNombre() + "'"
                                                + PIPE_SEPARATOR + "cierre: " + apuesta.getFechaCierre(),
                                usuario.getId(),
                                apuesta.getCodigoInvitacion(),
                                ENTIDAD_APUESTA);

                final Long creadoPorId = apuesta.getCreadaPor().getId();
                return new ApuestaDTO(apuesta.getId(), apuesta.getNombre(), apuesta.getEstado(),
                                apuesta.getCodigoInvitacion(), apuesta.getFechaCierre(), creadoPorId);
        }

        /**
         * Registra el pronóstico de un usuario para un partido dentro de una polla.
         * Valida que la polla esté abierta, que el usuario sea participante de ella
         * y que el partido no esté a 5 minutos o menos de comenzar.
         * El pronóstico incluye el marcador esperado y el resultado (LOCAL, VISITANTE o EMPATE)
         *
         * @param dto datos del pronóstico: id de usuario, id de polla, id de partido, goles y resultado esperado
         * @return {@link PronosticoDTO} con la información del pronóstico registrado
         * @throws ApuestaNotFoundException      si la polla no existe
         * @throws ApuestaCerradaException       si la polla no está en estado ABIERTA
         * @throws ParticipacionNotFoundException si el usuario no pertenece a la polla
         * @throws PartidoYaIniciadoException    si el partido está a 5 minutos o menos de iniciar
         */
        @Transactional
        @Override
        public PronosticoDTO registrarPronostico(PronosticoRequestDTO dto) {
                final Usuario usuario = usuarioService.obtenerEntidadPorId(dto.getUsuarioId());
                final Apuesta apuesta = apuestaRepository.findById(dto.getApuestaId())
                                .orElseThrow(() -> new ApuestaNotFoundException(APUESTA_NO_ENCONTRADA));

                if (!ESTADO_ABIERTA.equalsIgnoreCase(apuesta.getEstado())) {
                        throw new ApuestaCerradaException("La apuesta no está abierta para pronósticos");
                }

                participacionRepository.findByUsuarioIdAndApuestaId(usuario.getId(), apuesta.getId())
                                .orElseThrow(() -> new ParticipacionNotFoundException(
                                                "Usuario no pertenece a la polla"));

                final Partido partido = partidoService.obtenerPartidoEntidadPorId(dto.getPartidoId());
                if (partido.getFecha().isBefore(LocalDateTime.now().plusMinutes(5))) {
                        throw new PartidoYaIniciadoException(
                                        "No puedes registrar un pronostico a 5 minutos o menos del inicio del partido");
                }
                final Pronostico pronostico = new Pronostico();
                pronostico.setResultadoPronosticado(dto.getResultadoPronosticado());
                pronostico.setGolesLocalPronosticados(dto.getGolesLocalPronosticados());
                pronostico.setGolesVisitantePronosticados(dto.getGolesVisitantePronosticados());
                pronostico.setUsuario(usuario);
                pronostico.setApuesta(apuesta);
                pronostico.setPartido(partido);
                pronostico.setPuntosObtenidos(0);
                pronosticoRepository.save(pronostico);

                auditoriaService.registrar(
                                "PRONOSTICO_REGISTRADO",
                                usuario.getNombre() + " " + usuario.getApellido()
                                                + " registró pronóstico en polla '" + apuesta.getNombre() + "'"
                                                + PIPE_SEPARATOR + partido.getSeleccionLocal() + VS_SEPARATOR
                                                + partido.getSeleccionVisitante()
                                                + PIPE_SEPARATOR + dto.getGolesLocalPronosticados() + "-"
                                                + dto.getGolesVisitantePronosticados()
                                                + " (" + dto.getResultadoPronosticado() + ")",
                                usuario.getId(),
                                UUID.randomUUID().toString(),
                                ENTIDAD_PRONOSTICO);

                return new PronosticoDTO(pronostico.getId(), pronostico.getResultadoPronosticado(),
                                pronostico.getGolesLocalPronosticados(), pronostico.getGolesVisitantePronosticados(),
                                pronostico.getPuntosObtenidos(), pronostico.getUsuario().getId(),
                                pronostico.getApuesta().getId(), pronostico.getPartido().getId());
        }

        /**
         * Permite a un usuario unirse a una polla existente usando el código de invitación.
         * Si el código es válido y el usuario no está ya en la polla, se crea su participación
         * con 0 puntos. Se notifica al creador de la polla y se registra en auditoría
         *
         * @param codigo     código de invitación único de la polla
         * @param usuarioId  id del usuario que desea unirse
         * @return {@link ApuestaDTO} con la información de la polla a la que se unió
         * @throws CodigoInvalidoException      si el código no corresponde a ninguna polla
         * @throws UsuarioYaEnApuestaException  si el usuario ya es participante de esa polla
         */
        @Transactional
        @Override
        public ApuestaDTO unirseApuesta(String codigo, Long usuarioId) {
                final Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId);
                final Apuesta apuesta = apuestaRepository.findByCodigoInvitacion(codigo)
                                .orElseThrow(() -> new CodigoInvalidoException("Codigo no coincide"));

                participacionRepository.findByUsuarioIdAndApuestaId(usuario.getId(), apuesta.getId())
                                .ifPresent(p -> {
                                        throw new UsuarioYaEnApuestaException("Usuario ya está en la polla");
                                });

                final Participacion participacion = new Participacion();
                participacion.setUsuario(usuario);
                participacion.setApuesta(apuesta);
                participacion.setPuntos(0);
                participacionRepository.save(participacion);

                notificacionService.notificarApuestaUnirse(usuario, apuesta.getCreadaPor(), apuesta.getNombre());

                auditoriaService.registrar(
                                "APUESTA_UNIRSE",
                                usuario.getNombre() + " " + usuario.getApellido()
                                                + " se unió a la polla '" + apuesta.getNombre() + "'",
                                usuario.getId(),
                                apuesta.getCodigoInvitacion(),
                                ENTIDAD_APUESTA);

                final Long creadoPorId = apuesta.getCreadaPor().getId();
                return new ApuestaDTO(apuesta.getId(), apuesta.getNombre(), apuesta.getEstado(),
                                apuesta.getCodigoInvitacion(), apuesta.getFechaCierre(), creadoPorId);
        }

        /**
         * Edita un pronóstico existente. Valida que el pronóstico pertenezca al usuario
         * que hace la solicitud y que la polla siga abierta. Guarda los valores anteriores
         * en auditoría para tener trazabilidad del cambio realizado
         *
         * @param pronosticoId  id del pronóstico a editar
         * @param dto           nuevos valores del pronóstico (goles y resultado esperado)
         * @param correoUsuario correo del usuario que intenta editar, usado para verificar propiedad
         * @return {@link PronosticoDTO} con los datos actualizados del pronóstico
         * @throws PronosticoNotFoundException si el pronóstico no existe
         * @throws ApuestaCerradaException     si el pronóstico no pertenece al usuario o la polla está cerrada
         */
        @Transactional
        @Override
        public PronosticoDTO editarPronostico(Long pronosticoId, PronosticoRequestDTO dto, String correoUsuario) {
                final Pronostico pronostico = pronosticoRepository.findById(pronosticoId)
                                .orElseThrow(() -> new PronosticoNotFoundException(PRONOSTICO_NO_ENCONTRADO));
                if (!pronostico.getUsuario().getCorreoUsuario().equalsIgnoreCase(correoUsuario)) {
                        throw new ApuestaCerradaException("No puedes editar un pronóstico que no es tuyo");
                }

                final Apuesta apuesta = pronostico.getApuesta();
                if (!ESTADO_ABIERTA.equalsIgnoreCase(apuesta.getEstado())) {
                        throw new ApuestaCerradaException("No se puede editar un pronóstico de una polla cerrada");
                }

                final String resultadoAnterior = pronostico.getResultadoPronosticado();
                final int golesLocalAnterior = pronostico.getGolesLocalPronosticados();
                final int golesVisitanteAnterior = pronostico.getGolesVisitantePronosticados();

                final String resultadoPronosticado;
if (dto.getResultadoPronosticado() != null) {
    resultadoPronosticado = dto.getResultadoPronosticado();
} else {
    resultadoPronosticado = pronostico.getResultadoPronosticado();
}

                pronostico.setGolesLocalPronosticados(dto.getGolesLocalPronosticados());
                pronostico.setGolesVisitantePronosticados(dto.getGolesVisitantePronosticados());
                pronostico.setResultadoPronosticado(resultadoPronosticado);
                pronosticoRepository.save(pronostico);

                auditoriaService.registrar(
                                "PRONOSTICO_EDITADO",
                                pronostico.getUsuario().getNombre() + " " + pronostico.getUsuario().getApellido()
                                                + " editó pronóstico en polla '" + apuesta.getNombre() + "'"
                                                + PIPE_SEPARATOR + pronostico.getPartido().getSeleccionLocal()
                                                + VS_SEPARATOR + pronostico.getPartido().getSeleccionVisitante()
                                                + PIPE_SEPARATOR + "antes: " + golesLocalAnterior + "-"
                                                + golesVisitanteAnterior + " (" + resultadoAnterior + ")"
                                                + PIPE_SEPARATOR + "después: " + dto.getGolesLocalPronosticados() + "-"
                                                + dto.getGolesVisitantePronosticados() + " (" + resultadoPronosticado
                                                + ")",
                                pronostico.getUsuario().getId(),
                                UUID.randomUUID().toString(),
                                ENTIDAD_PRONOSTICO);

                return new PronosticoDTO(
                                pronostico.getId(),
                                pronostico.getResultadoPronosticado(),
                                pronostico.getGolesLocalPronosticados(),
                                pronostico.getGolesVisitantePronosticados(),
                                pronostico.getPuntosObtenidos(),
                                pronostico.getUsuario().getId(),
                                pronostico.getApuesta().getId(),
                                pronostico.getPartido().getId());
        }

        /**
         * Elimina un pronóstico existente. Verifica que pertenezca al usuario que solicita
         * la eliminación y que la polla siga abierta. La operación queda registrada en auditoría
         * con el nombre del partido y la polla afectados
         *
         * @param pronosticoId  id del pronóstico a eliminar
         * @param correoUsuario correo del usuario que intenta eliminar, usado para verificar propiedad
         * @throws PronosticoNotFoundException si el pronóstico no existe
         * @throws ApuestaCerradaException     si el pronóstico no pertenece al usuario o la polla está cerrada
         */
        @Transactional
        @Override
        public void eliminarPronostico(Long pronosticoId, String correoUsuario) {
                final Pronostico pronostico = pronosticoRepository.findById(pronosticoId)
                                .orElseThrow(() -> new PronosticoNotFoundException(PRONOSTICO_NO_ENCONTRADO));
                if (!pronostico.getUsuario().getCorreoUsuario().equalsIgnoreCase(correoUsuario)) {
                        throw new ApuestaCerradaException("No puedes eliminar un pronóstico que no es tuyo");
                }
                final Apuesta apuesta = pronostico.getApuesta();
                if (!ESTADO_ABIERTA.equalsIgnoreCase(apuesta.getEstado())) {
                        throw new ApuestaCerradaException("No se puede eliminar un pronóstico de una polla cerrada");
                }

                final Long usuarioId = pronostico.getUsuario().getId();
                final String nombreUsuario = pronostico.getUsuario().getNombre() + " "
                                + pronostico.getUsuario().getApellido();
                final String nombreApuesta = apuesta.getNombre();
                final String nombrePartido = pronostico.getPartido().getSeleccionLocal() + VS_SEPARATOR
                                + pronostico.getPartido().getSeleccionVisitante();

                pronosticoRepository.delete(pronostico);

                auditoriaService.registrar(
                                "PRONOSTICO_ELIMINADO",
                                nombreUsuario + " eliminó su pronóstico en polla '" + nombreApuesta + "'"
                                                + PIPE_SEPARATOR + nombrePartido,
                                usuarioId,
                                UUID.randomUUID().toString(),
                                ENTIDAD_PRONOSTICO);
        }

        /**
         * Retorna el ranking actual de participantes de una polla,
         * ordenado de mayor a menor puntaje
         *
         * @param apuestaId id de la polla
         * @return lista de {@link ParticipacionDTO} ordenada por puntos de forma descendente
         */
        @Transactional(readOnly = true)
        @Override
        public List<ParticipacionDTO> obtenerRanking(Long apuestaId) {
                return participacionRepository.findByApuestaIdOrderByPuntosDesc(apuestaId).stream()
                                .map(p -> new ParticipacionDTO(p.getId(), p.getUsuario().getId(),
                                                p.getApuesta().getId(), p.getPuntos(), p.getPosicionRanking()))
                                .toList();
        }

        /**
         * Calcula y asigna los puntos finales a todos los pronósticos de una polla cerrada.
         * Primero reinicia los puntos de todos los participantes, luego sincroniza los resultados
         * reales de los partidos con la API externa, evalúa cada pronóstico usando la lógica
         * de puntuación, actualiza el ranking final y notifica a cada participante su posición y puntos.
         * Solo puede ejecutarse si la polla está en estado CERRADA
         *
         * @param apuestaId id de la polla a finalizar
         * @return lista de {@link PronosticoDTO} con los puntos obtenidos por cada pronóstico
         * @throws ApuestaNotFoundException si la polla no existe
         * @throws EstadoInvalidoException  si la polla no está en estado CERRADA
         */
        @Transactional
        @Override
        public List<PronosticoDTO> calcularPuntos(Long apuestaId) {
                final Apuesta apuesta = apuestaRepository.findById(apuestaId)
                                .orElseThrow(() -> new ApuestaNotFoundException("La apuesta no existe"));

                if (!ESTADO_CERRADA.equalsIgnoreCase(apuesta.getEstado())) {
                        throw new EstadoInvalidoException("La apuesta debe estar cerrada para calcular puntos");
                }

                final List<Participacion> participaciones = participacionRepository.findByApuestaId(apuestaId);
                for (final Participacion p : participaciones) {
                        p.setPuntos(0);
                        participacionRepository.save(p);
                }

                final List<Pronostico> pronosticos = pronosticoRepository.findByApuestaId(apuestaId);
                pronosticos.stream()
                                .map(p -> p.getPartido().getFecha().toLocalDate().toString())
                                .distinct()
                                .forEach(fecha -> partidoService.sincronizarPorFechaYLiga(fecha, 1, 2026));

                final List<PronosticoDTO> resultado = new ArrayList<>();
                for (final Pronostico pronostico : pronosticoRepository.findByApuestaId(apuestaId)) {
                        final Partido partido = pronostico.getPartido();
                        if (partido.getGolesLocal() == null || partido.getGolesVisitante() == null) {
                                continue;
                        }

                        final String resultadoReal = determinarResultado(partido);
                        final int puntos = calcularPuntosPronostico(partido, pronostico, resultadoReal);

                        pronostico.setPuntosObtenidos(puntos);
                        pronosticoRepository.save(pronostico);

                        final Participacion participacion = participacionRepository
                                        .findByUsuarioIdAndApuestaId(pronostico.getUsuario().getId(), apuestaId)
                                        .orElseThrow(() -> new ParticipacionNotFoundException(
                                                        PARTICIPACION_NO_ENCONTRADA));
                        participacion.setPuntos(puntos + participacion.getPuntos());
                        participacionRepository.save(participacion);

                        resultado.add(new PronosticoDTO(pronostico.getId(), pronostico.getResultadoPronosticado(),
                                        pronostico.getGolesLocalPronosticados(),
                                        pronostico.getGolesVisitantePronosticados(),
                                        pronostico.getPuntosObtenidos(), pronostico.getUsuario().getId(),
                                        pronostico.getApuesta().getId(), pronostico.getPartido().getId()));
                }

                int posicion = 1;
                for (final Participacion p : participacionRepository.findByApuestaIdOrderByPuntosDesc(apuestaId)) {
                        p.setPosicionRanking(posicion);
                        posicion++;
                        participacionRepository.save(p);
                }

                final List<Participacion> participacionesFinales = participacionRepository
                                .findByApuestaIdOrderByPuntosDesc(apuestaId);

                final StringBuilder rankingLog = new StringBuilder();
                for (final Participacion p : participacionesFinales) {
                        notificacionService.notificarPuntosCalculados(
                                        p.getUsuario(),
                                        apuesta.getNombre(),
                                        p.getPosicionRanking(),
                                        p.getPuntos());
                        rankingLog.append(p.getPosicionRanking())
                                        .append(". ").append(p.getUsuario().getCorreoUsuario())
                                        .append(' ').append(p.getPuntos()).append("pts | ");
                }
                apuesta.setPuntosCalculados(true);
                apuestaRepository.save(apuesta);

                auditoriaService.registrar(
                                "APUESTA_FINALIZADA",
                                "Polla '" + apuesta.getNombre() + "' finalizada" + PIPE_SEPARATOR + "ranking: "
                                                + rankingLog,
                                null,
                                apuesta.getCodigoInvitacion(),
                                ENTIDAD_APUESTA);

                return resultado;
        }

        /**
         * Determina el resultado real de un partido comparando los goles del local y visitante.
         * Retorna "LOCAL" si ganó el equipo de casa, "VISITANTE" si ganó el de fuera,
         * o "EMPATE" si terminaron iguales
         *
         * @param partido partido con los goles reales registrados
         * @return cadena con el resultado: "LOCAL", "VISITANTE" o "EMPATE"
         */
        private String determinarResultado(final Partido partido) {
                if (partido.getGolesLocal() > partido.getGolesVisitante()) {
                        return "LOCAL";
                } else if (partido.getGolesLocal() < partido.getGolesVisitante()) {
                        return "VISITANTE";
                } else {
                        return "EMPATE";
                }
        }

        /**
         * Calcula los puntos obtenidos por un pronóstico comparándolo con el resultado real del partido.
         * La lógica de puntuación es: 3 puntos si el marcador exacto coincide,
         * 2 puntos si el resultado (LOCAL/VISITANTE/EMPATE) coincide,
         * y 1 punto adicional si el total de goles pronosticados es igual al total real.
         * Los criterios son acumulables, por lo que el máximo posible es 6 puntos
         *
         * @param partido      partido con los goles reales
         * @param pronostico   pronóstico del usuario con los goles y resultado esperados
         * @param resultadoReal resultado real calculado ("LOCAL", "VISITANTE" o "EMPATE")
         * @return total de puntos obtenidos por el pronóstico
         */
        private int calcularPuntosPronostico(final Partido partido, final Pronostico pronostico,
                        final String resultadoReal) {
                int puntos = 0;
                if (pronostico.getGolesLocalPronosticados().equals(partido.getGolesLocal())
                                && pronostico.getGolesVisitantePronosticados().equals(partido.getGolesVisitante())) {
                        puntos += 3;
                }
                if (pronostico.getResultadoPronosticado().equals(resultadoReal)) {
                        puntos += 2;
                }
                if ((pronostico.getGolesLocalPronosticados()
                                + pronostico.getGolesVisitantePronosticados()) == (partido.getGolesLocal()
                                                + partido.getGolesVisitante())) {
                        puntos += 1;
                }
                return puntos;
        }

        /**
         * Cambia el estado de una polla de ABIERTA a CERRADA, impidiendo que se
         * registren o editen pronósticos a partir de ese momento
         *
         * @param apuestaId id de la polla a cerrar
         * @return {@link ApuestaDTO} con el estado actualizado a CERRADA
         * @throws ApuestaNotFoundException si la polla no existe
         * @throws ApuestaCerradaException  si la polla ya estaba cerrada
         */
        @Transactional
        @Override
        public ApuestaDTO cerrarApuesta(Long apuestaId) {
                final Apuesta apuesta = apuestaRepository.findById(apuestaId)
                                .orElseThrow(() -> new ApuestaNotFoundException(APUESTA_NO_ENCONTRADA));
                if (!ESTADO_ABIERTA.equalsIgnoreCase(apuesta.getEstado())) {
                        throw new ApuestaCerradaException("La apuesta ya no está abierta");
                }
                apuesta.setEstado(ESTADO_CERRADA);
                apuestaRepository.save(apuesta);

                return new ApuestaDTO(apuesta.getId(), apuesta.getNombre(), apuesta.getEstado(),
                                apuesta.getCodigoInvitacion(), apuesta.getFechaCierre(),
                                apuesta.getCreadaPor().getId());
        }

        /**
         * Busca y retorna la información de una polla por su id
         *
         * @param apuestaId id de la polla a consultar
         * @return {@link ApuestaDTO} con los datos de la polla
         * @throws ApuestaNotFoundException si la polla no existe
         */
        @Transactional(readOnly = true)
        @Override
        public ApuestaDTO obtenerApuesta(Long apuestaId) {
                final Apuesta apuesta = apuestaRepository.findById(apuestaId)
                                .orElseThrow(() -> new ApuestaNotFoundException(APUESTA_NO_ENCONTRADA));
                return new ApuestaDTO(apuesta.getId(), apuesta.getNombre(), apuesta.getEstado(),
                                apuesta.getCodigoInvitacion(), apuesta.getFechaCierre(),
                                apuesta.getCreadaPor().getId());
        }

        /**
         * Retorna la lista de todos los participantes de una polla con sus puntos
         * y posición en el ranking actual
         *
         * @param apuestaId id de la polla
         * @return lista de {@link ParticipacionDTO} con la información de cada participante
         */
        @Transactional(readOnly = true)
        @Override
        public List<ParticipacionDTO> listarParticipantes(Long apuestaId) {
                return participacionRepository.findByApuestaId(apuestaId).stream()
                                .map(p -> new ParticipacionDTO(p.getId(), p.getUsuario().getId(),
                                                p.getApuesta().getId(), p.getPuntos(), p.getPosicionRanking()))
                                .toList();
        }

        /**
         * Retorna todas las pollas en las que un usuario participa,
         * ya sea como creador o como participante invitado
         *
         * @param usuarioId id del usuario
         * @return lista de {@link ApuestaDTO} con las pollas del usuario
         */
        @Transactional(readOnly = true)
        @Override
        public List<ApuestaDTO> listarApuestasPorUsuario(Long usuarioId) {
                return participacionRepository.findByUsuarioId(usuarioId).stream()
                                .map(participacion -> {
                                        final Apuesta apuesta = participacion.getApuesta();
                                        return new ApuestaDTO(
                                                        apuesta.getId(),
                                                        apuesta.getNombre(),
                                                        apuesta.getEstado(),
                                                        apuesta.getCodigoInvitacion(),
                                                        apuesta.getFechaCierre(),
                                                        apuesta.getCreadaPor().getId());
                                })
                                .toList();
        }

        /**
         * Busca y retorna un pronóstico específico por su id
         *
         * @param pronosticoId id del pronóstico a consultar
         * @return {@link PronosticoDTO} con los datos del pronóstico
         * @throws PronosticoNotFoundException si el pronóstico no existe
         */
        @Transactional(readOnly = true)
        @Override
        public PronosticoDTO verificarPronostico(Long pronosticoId) {
                final Pronostico pronostico = pronosticoRepository.findById(pronosticoId)
                                .orElseThrow(() -> new PronosticoNotFoundException(PRONOSTICO_NO_ENCONTRADO));
                return new PronosticoDTO(pronostico.getId(), pronostico.getResultadoPronosticado(),
                                pronostico.getGolesLocalPronosticados(), pronostico.getGolesVisitantePronosticados(),
                                pronostico.getPuntosObtenidos(), pronostico.getUsuario().getId(),
                                pronostico.getApuesta().getId(), pronostico.getPartido().getId());
        }

        /**
         * Método ejecutado automáticamente (scheduled) que busca todas las pollas cerradas
         * cuyo cálculo de puntos aún no ha sido realizado y los calcula
         */
        @Transactional
        @Override
        public void calcularPuntosAutomatico() {
                apuestaRepository.findByEstadoAndPuntosCalculadosFalse(ESTADO_CERRADA)
                                .forEach(apuesta -> calcularPuntos(apuesta.getId()));
        }

        /**
         * Método ejecutado automáticamente (scheduled) que cierra todas las pollas
         * cuya fecha de cierre ya venció o está a 5 minutos de vencer
         */
        @Transactional
        @Override
        public void cerrarApuestasVencidas() {
                apuestaRepository.findByEstadoAndFechaCierreBefore(ESTADO_ABIERTA, LocalDateTime.now().plusMinutes(5))
                                .forEach(apuesta -> cerrarApuesta(apuesta.getId()));
        }

        /**
         * Retorna todos los pronósticos que un usuario hizo dentro de una polla específica
         *
         * @param apuestaId id de la polla
         * @param usuarioId id del usuario
         * @return lista de {@link PronosticoDTO} con los pronósticos del usuario en esa polla
         */
        @Transactional(readOnly = true)
        @Override
        public List<PronosticoDTO> misPronosticos(Long apuestaId, Long usuarioId) {
                return pronosticoRepository.findByApuestaIdAndUsuarioId(apuestaId, usuarioId).stream()
                                .map(p -> new PronosticoDTO(
                                                p.getId(),
                                                p.getResultadoPronosticado(),
                                                p.getGolesLocalPronosticados(),
                                                p.getGolesVisitantePronosticados(),
                                                p.getPuntosObtenidos(),
                                                p.getUsuario().getId(),
                                                p.getApuesta().getId(),
                                                p.getPartido().getId()))
                                .toList();
        }

        /**
         * Calcula puntos parciales para los pronósticos de partidos que ya terminaron
         * dentro de una polla que aún puede estar abierta. Solo procesa pronósticos
         * que tengan resultado real disponible y que no hayan sido puntuados previamente.
         * Actualiza el ranking al finalizar
         *
         * @param apuestaId id de la polla
         * @return lista de {@link PronosticoDTO} con los pronósticos que fueron puntuados en esta ejecución
         * @throws ApuestaNotFoundException si la polla no existe
         */
        @Transactional
        @Override
        public List<PronosticoDTO> calcularPuntosParciales(Long apuestaId) {
                apuestaRepository.findById(apuestaId)
                                .orElseThrow(() -> new ApuestaNotFoundException(APUESTA_NO_ENCONTRADA));

                final List<Pronostico> pronosticos = pronosticoRepository.findByApuestaId(apuestaId);
                final List<PronosticoDTO> resultado = new ArrayList<>();

                for (final Pronostico pronostico : pronosticos) {
                        final Partido partido = pronostico.getPartido();

                        if (partido.getGolesLocal() == null || partido.getGolesVisitante() == null
                                        || (pronostico.getPuntosObtenidos() != null
                                                        && pronostico.getPuntosObtenidos() > 0)) {
                                continue;
                        }

                        final String resultadoReal = determinarResultado(partido);
                        final int puntos = calcularPuntosPronostico(partido, pronostico, resultadoReal);

                        pronostico.setPuntosObtenidos(puntos);
                        pronosticoRepository.save(pronostico);

                        final Participacion participacion = participacionRepository
                                        .findByUsuarioIdAndApuestaId(pronostico.getUsuario().getId(), apuestaId)
                                        .orElseThrow(() -> new ParticipacionNotFoundException(
                                                        PARTICIPACION_NO_ENCONTRADA));
                        participacion.setPuntos(participacion.getPuntos() + puntos);
                        participacionRepository.save(participacion);

                        resultado.add(new PronosticoDTO(
                                        pronostico.getId(),
                                        pronostico.getResultadoPronosticado(),
                                        pronostico.getGolesLocalPronosticados(),
                                        pronostico.getGolesVisitantePronosticados(),
                                        pronostico.getPuntosObtenidos(),
                                        pronostico.getUsuario().getId(),
                                        pronostico.getApuesta().getId(),
                                        pronostico.getPartido().getId()));
                }

                int posicion = 1;
                for (final Participacion p : participacionRepository.findByApuestaIdOrderByPuntosDesc(apuestaId)) {
                        p.setPosicionRanking(posicion);
                        posicion++;
                        participacionRepository.save(p);
                }

                return resultado;
        }

        /**
         * Retorna todas las pollas registradas en el sistema sin ningún filtro
         *
         * @return lista de {@link ApuestaDTO} con todas las pollas existentes
         */
        @Transactional(readOnly = true)
        @Override
        public List<ApuestaDTO> listarTodas() {
                List<Apuesta> apuestas = apuestaRepository.findAll();
                List<ApuestaDTO> resultado = new ArrayList<>();
                for (Apuesta a : apuestas) {
                        resultado.add(new ApuestaDTO(a.getId(), a.getNombre(), a.getEstado(),
                                        a.getCodigoInvitacion(), a.getFechaCierre(), a.getCreadaPor().getId()));
                }
                return resultado;
        }

        /**
         * Elimina una polla junto con todos sus pronósticos y participaciones asociadas.
         * La eliminación es en cascada: primero se borran los pronósticos, luego las participaciones
         * y finalmente la polla
         *
         * @param apuestaId id de la polla a eliminar
         * @throws ApuestaNotFoundException si la polla no existe
         */
        @Transactional
        @Override
        public void eliminarApuesta(Long apuestaId) {
                Apuesta apuesta = apuestaRepository.findById(apuestaId)
                                .orElseThrow(() -> new ApuestaNotFoundException(APUESTA_NO_ENCONTRADA));
                pronosticoRepository.deleteByApuestaId(apuestaId);
                participacionRepository.deleteByApuestaId(apuestaId);
                apuestaRepository.delete(apuesta);
        }

        /**
         * Retorna las pollas de un usuario incluyendo la lista completa de participantes de cada una.
         * Optimiza las consultas agrupando todas las participaciones de las pollas del usuario
         * en una sola consulta, evitando el problema de N+1
         *
         * @param usuarioId id del usuario
         * @return lista de {@link ApuestaConParticipantesDTO} con cada polla y sus participantes
         */
        @Transactional(readOnly = true)
        @Override
        public List<ApuestaConParticipantesDTO> listarApuestasPorUsuarioCompleto(Long usuarioId) {
                final List<Participacion> misParticipaciones = participacionRepository
                                .findByUsuarioIdConApuesta(usuarioId);

                final List<Long> apuestaIds = misParticipaciones.stream()
                                .map(p -> p.getApuesta().getId())
                                .toList();

                final Map<Long, List<ParticipacionDTO>> porApuesta = participacionRepository
                                .findByApuestaIdInConUsuario(apuestaIds).stream()
                                .collect(Collectors.groupingBy(
                                                p -> p.getApuesta().getId(),
                                                Collectors.mapping(
                                                                p -> new ParticipacionDTO(p.getId(),
                                                                                p.getUsuario().getId(),
                                                                                p.getApuesta().getId(), p.getPuntos(),
                                                                                p.getPosicionRanking()),
                                                                Collectors.toList())));

                return misParticipaciones.stream()
                                .map(participacion -> {
                                        final Apuesta apuesta = participacion.getApuesta();
                                        return new ApuestaConParticipantesDTO(
                                                        apuesta.getId(), apuesta.getNombre(), apuesta.getEstado(),
                                                        apuesta.getCodigoInvitacion(), apuesta.getFechaCierre(),
                                                        apuesta.getCreadaPor().getId(),
                                                        porApuesta.getOrDefault(apuesta.getId(), List.of()));
                                })
                                .toList();
        }
}