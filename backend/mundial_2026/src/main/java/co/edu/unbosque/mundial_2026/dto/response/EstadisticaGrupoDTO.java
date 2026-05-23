package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para las estadisticas de grupos
 * Contiene las tablas de posiciones de cada grupo
 */
public class EstadisticaGrupoDTO {

    /**
     * Tablas de posiciones de los grupos
     */
    @JsonProperty("standings")
    private List<List<PosicionDTO>> tablas;

    /**
     * Obtiene las tablas de posiciones
     *
     * @return tablas de posiciones
     */
    public List<List<PosicionDTO>> getTablas() {
        return tablas;
    }

    /**
     * Establece las tablas de posiciones
     *
     * @param tablas tablas de posiciones
     */
    public void setTablas(List<List<PosicionDTO>> tablas) {
        this.tablas = tablas;
    }
}