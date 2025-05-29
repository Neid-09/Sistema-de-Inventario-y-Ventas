package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    long countByRolIdRol(Integer idRol);

    // Nuevos métodos para filtrar por estado
    List<Usuario> findByEstado(boolean estado);
    List<Usuario> findByEstadoTrue();
    List<Usuario> findByEstadoFalse();

    @Query("SELECT u FROM Usuario u JOIN u.rol r WHERE r.nombre = :nombreRol")
    List<Usuario> findByRolNombre(@Param("nombreRol") String nombreRol);

    @Query("SELECT u FROM Usuario u JOIN u.permisosPersonalizados p WHERE p.nombre = :nombrePermiso")
    List<Usuario> findByPermiso(@Param("nombrePermiso") String nombrePermiso);

    @Query("SELECT DISTINCT u FROM Usuario u " +
            "LEFT JOIN u.permisosPersonalizados p1 " +
            "LEFT JOIN u.rol r " +
            "LEFT JOIN r.permisos p2 " +
            "WHERE p1.nombre = :nombrePermiso OR p2.nombre = :nombrePermiso")
    List<Usuario> findByPermisoEffective(@Param("nombrePermiso") String nombrePermiso);
}