package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Vendedor.
 * Proporciona métodos CRUD y consultas personalizadas para Vendedor.
 */
@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {

    // Buscar vendedores por el nombre de su Usuario asociado (ignorando mayúsculas/minúsculas)
    List<Vendedor> findByUsuarioNombreContainingIgnoreCase(String nombre);

    // Buscar vendedores cuyo Usuario asociado está activo
    List<Vendedor> findByUsuarioEstadoTrue();

    // Buscar vendedores cuyo Usuario asociado está inactivo
    List<Vendedor> findByUsuarioEstadoFalse();

    // Nuevo método para buscar por ID cargando el Usuario asociado
    @Query("SELECT v FROM Vendedor v LEFT JOIN FETCH v.usuario WHERE v.idVendedor = :id")
    Optional<Vendedor> findByIdWithUsuario(@Param("id") Integer id);

    // Puedes añadir más métodos con JOIN FETCH si necesitas cargar el usuario en otras consultas
    // Ejemplo: Buscar activos con usuario
    // @Query("SELECT v FROM Vendedor v LEFT JOIN FETCH v.usuario u WHERE u.estado = true")
    // List<Vendedor> findActivosWithUsuario();

}
