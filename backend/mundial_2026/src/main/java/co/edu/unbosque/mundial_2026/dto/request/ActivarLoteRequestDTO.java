package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

/**
 * DTO de solicitud para activar un lote de entidades en masa.
 * <p>
 * Contiene la lista de identificadores de los registros que se desean activar
 * en una sola operación.
 * </p>
 */
public class ActivarLoteRequestDTO {

    /** Lista de identificadores únicos de los registros que se desean activar. */
    private List<Long> ids;

    /**
     * Obtiene la lista de identificadores a activar.
     *
     * @return lista de IDs seleccionados para activación masiva
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * Establece la lista de identificadores a activar.
     *
     * @param ids lista de IDs que se desean activar en lote
     */
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}