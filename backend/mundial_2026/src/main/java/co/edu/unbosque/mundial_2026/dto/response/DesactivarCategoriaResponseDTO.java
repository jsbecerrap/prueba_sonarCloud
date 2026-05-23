package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

/**
 * DTO de respuesta para la desactivacion de categorias
 * Contiene la categoria desactivada y los productos afectados
 */
public class DesactivarCategoriaResponseDTO {

    /**
     * Categoria desactivada
     */
    private CategoriaResponseDTO categoria;

    /**
     * Lista de productos afectados por la desactivacion de la categoria
     */
    private List<ProductoResponseDTO> productosAfectados;

    /**
     * Obtiene la categoria desactivada
     *
     * @return categoria desactivada
     */
    public CategoriaResponseDTO getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoria desactivada
     *
     * @param categoria categoria desactivada
     */
    public void setCategoria(CategoriaResponseDTO categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene la lista de productos afectados
     *
     * @return lista de productos afectados
     */
    public List<ProductoResponseDTO> getProductosAfectados() {
        return productosAfectados;
    }

    /**
     * Establece la lista de productos afectados
     *
     * @param productosAfectados lista de productos afectados
     */
    public void setProductosAfectados(List<ProductoResponseDTO> productosAfectados) {
        this.productosAfectados = productosAfectados;
    }
}