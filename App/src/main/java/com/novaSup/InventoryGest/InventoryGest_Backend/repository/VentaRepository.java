package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar Query
import org.springframework.data.repository.query.Param; // Importar Param

import java.util.List; // Importar List
import java.util.Optional; // Importar Optional

// Corregido: Cambiado el tipo de ID de Long a Integer para que coincida con el modelo Venta
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Método para buscar por ID cargando Vendedor y Usuario asociado
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario WHERE v.idVenta = :id")
    Optional<Venta> findWithDetailsById(@Param("id") Integer id);

    // Método para buscar todas las ventas cargando Vendedor y Usuario asociado
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario")
    List<Venta> findAllWithDetails();

    // Método para buscar ventas por cliente cargando Vendedor y Usuario asociado
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario WHERE v.cliente.idCliente = :idCliente")
    List<Venta> findByIdClienteWithDetails(@Param("idCliente") Integer idCliente);

    // Método para buscar ventas por vendedor cargando Vendedor y Usuario asociado
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario WHERE ven.idVendedor = :idVendedor")
    List<Venta> findByIdVendedorWithDetails(@Param("idVendedor") Integer idVendedor);

    // Métodos originales de JpaRepository (findById, findAll, save, etc.) siguen disponibles
    // pero no garantizan la carga EAGER de las relaciones sin JOIN FETCH.
}