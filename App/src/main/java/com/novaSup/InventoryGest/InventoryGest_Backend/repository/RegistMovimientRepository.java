package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistMovimientRepository extends JpaRepository<RegistMovimient, Integer> {
    // Buscar por ID de producto
    List<RegistMovimient> findByProductoIdProducto(Integer idProducto);
    
    // Buscar por tipo de movimiento
    List<RegistMovimient> findByTipoMovimiento(String tipoMovimiento);
    
    // Buscar por ID de proveedor
    List<RegistMovimient> findByProveedorIdProveedor(Integer idProveedor);
    
    // Buscar por rango de fechas
    List<RegistMovimient> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Buscar por tipo de movimiento y ID de producto
    List<RegistMovimient> findByTipoMovimientoAndProductoIdProducto(String tipoMovimiento, Integer idProducto);
    
    // Buscar por fecha y tipo de movimiento
    List<RegistMovimient> findByFechaBetweenAndTipoMovimiento(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoMovimiento);
}