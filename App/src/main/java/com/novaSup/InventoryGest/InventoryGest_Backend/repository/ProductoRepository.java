package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstadoTrue();
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByCategoria_IdCategoria(Integer idCategoria);

    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estado = true")
    List<Producto> findProductosConStockBajo();

    // Nuevos métodos para búsqueda y filtrado
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCodigoContainingIgnoreCase(String codigo);
    List<Producto> findByEstado(Boolean estado);
    List<Producto> findByCategoria_IdCategoriaAndEstado(Integer idCategoria, Boolean estado);

    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%',:nombre,'%'))) AND " +
            "(:codigo IS NULL OR LOWER(p.codigo) LIKE LOWER(CONCAT('%',:codigo,'%'))) AND " +
            "(:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) AND " +
            "(:estado IS NULL OR p.estado = :estado)")
    List<Producto> buscarPorFiltros(
            @Param("nombre") String nombre,
            @Param("codigo") String codigo,
            @Param("idCategoria") Integer idCategoria,
            @Param("estado") Boolean estado);

    boolean existsByCodigo(String codigo);
    List<Producto> findByProveedor_IdProveedor(Integer idProveedor);
}