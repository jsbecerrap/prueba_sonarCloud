package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

/**
 * DTO de respuesta para reactivacion de categoria
 * Contiene la categoria y los productos asociados
 */
public class ReactivarCategoriaResponseDTO {

    /**
     * Categoria reactivada
     */
    private CategoriaResponseDTO categoria;

    /**
     * Lista de productos asociados a la categoria
     */
    private List<ProductoResponseDTO> productos;

    /**
     * Obtiene la categoria reactivada
     *
     * @return categoria reactivada
     */
    public CategoriaResponseDTO getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoria reactivada
     *
     * @param categoria categoria reactivada
     */
    public void setCategoria(CategoriaResponseDTO categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene los productos asociados
     *
     * @return lista de productos
     */
    public List<ProductoResponseDTO> getProductos() {
        return productos;
    }

    /**
     * Establece los productos asociados
     *
     * @param productos lista de productos
     */
    public void setProductos(List<ProductoResponseDTO> productos) {
        this.productos = productos;
    }
}