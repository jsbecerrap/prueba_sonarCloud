package co.edu.unbosque.mundial_2026.dto.request;


public class EntradaRequestDTO {

    private Long partidoId;
    private Integer cantidad;
    private String categoria;
    private String sector;

    public EntradaRequestDTO() {
        //Constructor vacio
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}