package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    // Métodos de búsqueda personalizados si son necesarios, por ejemplo:
    // Optional<Factura> findByNumeroFactura(String numeroFactura);

    // Método para obtener una Factura por ID y cargar eager las relaciones Venta, Cliente, Vendedor, Usuario y DetallesVenta
    @Query("SELECT f FROM Factura f JOIN FETCH f.venta v JOIN FETCH v.cliente c JOIN FETCH v.vendedor vend JOIN FETCH vend.usuario u JOIN FETCH v.detallesVenta dv WHERE f.id = :idFactura")
    Optional<Factura> findByIdWithVenta(@Param("idFactura") int idFactura);
} 