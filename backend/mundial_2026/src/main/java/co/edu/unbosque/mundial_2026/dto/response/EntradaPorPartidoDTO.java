package co.edu.unbosque.mundial_2026.dto.response;

public class EntradaPorPartidoDTO {
    private Long partidoId;
    private String local;
    private String visitante;
    private String ronda;
    private String estadio;
    private int cantidadVendida;
    private double ingresoTotal;

    public EntradaPorPartidoDTO() {
    }

    public EntradaPorPartidoDTO(Long partidoId, String local, String visitante, String ronda, String estadio,
            int cantidadVendida, double ingresoTotal) {
        this.partidoId = partidoId;
        this.local = local;
        this.visitante = visitante;
        this.ronda = ronda;
        this.estadio = estadio;
        this.cantidadVendida = cantidadVendida;
        this.ingresoTotal = ingresoTotal;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getVisitante() {
        return visitante;
    }

    public void setVisitante(String visitante) {
        this.visitante = visitante;
    }

    public String getRonda() {
        return ronda;
    }

    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}