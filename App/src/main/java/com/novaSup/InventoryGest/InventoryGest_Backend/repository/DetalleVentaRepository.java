package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    // Puedes añadir métodos de consulta personalizados aquí si los necesitas.
    // Por ejemplo, para buscar todos los detalles de una venta específica:
    List<DetalleVenta> findByVentaIdVenta(Integer idVenta);

    // Para buscar detalles por producto:
    List<DetalleVenta> findByProductoIdProducto(Integer idProducto);

    // Para buscar detalles por lote:
    List<DetalleVenta> findByLoteIdLote(Integer idLote);
}