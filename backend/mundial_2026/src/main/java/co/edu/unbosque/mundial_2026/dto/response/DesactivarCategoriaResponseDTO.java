package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

public class DesactivarCategoriaResponseDTO {

    private CategoriaResponseDTO categoria;
    private List<ProductoResponseDTO> productosAfectados;

    public CategoriaResponseDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponseDTO categoria) {
        this.categoria = categoria;
    }

    public List<ProductoResponseDTO> getProductosAfectados() {
        return productosAfectados;
    }

    public void setProductosAfectados(List<ProductoResponseDTO> productosAfectados) {
        this.productosAfectados = productosAfectados;
    }
}