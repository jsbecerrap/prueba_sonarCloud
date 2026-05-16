package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

public class ReactivarCategoriaResponseDTO {

    private CategoriaResponseDTO categoria;
    private List<ProductoResponseDTO> productos;

    public CategoriaResponseDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponseDTO categoria) {
        this.categoria = categoria;
    }

    public List<ProductoResponseDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoResponseDTO> productos) {
        this.productos = productos;
    }
}