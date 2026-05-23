package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para las tablas de posiciones
 * Contiene la informacion de las estadisticas de grupos
 */
public class LigaStandingDTO {

    /**
     * Tablas de posiciones de la liga
     */
    @JsonProperty("league")
    private EstadisticaGrupoDTO tablas;

    /**
     * Obtiene las tablas de posiciones
     *
     * @return tablas de posiciones
     */
    public EstadisticaGrupoDTO getTablas() {
        return tablas;
    }

    /**
     * Establece las tablas de posiciones
     *
     * @param tablas tablas de posiciones
     */
    public void setTablas(EstadisticaGrupoDTO tablas) {
        this.tablas = tablas;
    }
}