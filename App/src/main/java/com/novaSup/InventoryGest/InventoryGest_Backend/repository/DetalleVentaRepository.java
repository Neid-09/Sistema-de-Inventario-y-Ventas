package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    // Puedes añadir métodos de consulta personalizados aquí si los necesitas.
    // Por ejemplo, para buscar todos los detalles de una venta específica:
    List<DetalleVenta> findByVentaIdVenta(Integer idVenta);

    // Para buscar detalles por producto:
    List<DetalleVenta> findByProductoIdProducto(Integer idProducto);

    // Para buscar detalles de venta que usaron un lote específico
    @Query("SELECT dv FROM DetalleVenta dv JOIN dv.detalleLotesUsados dlu JOIN dlu.lote l WHERE l.idLote = :idLote")
    List<DetalleVenta> findByLoteId(@Param("idLote") Integer idLote);
}