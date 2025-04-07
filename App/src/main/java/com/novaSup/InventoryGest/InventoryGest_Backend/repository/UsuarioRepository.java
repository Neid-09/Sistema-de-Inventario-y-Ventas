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

    @Query("SELECT u FROM Usuario u JOIN u.rol r WHERE r.nombre = :nombreRol")
    List<Usuario> findByRolNombre(@Param("nombreRol") String nombreRol);

    @Query("SELECT u FROM Usuario u JOIN u.permisosPersonalizados p WHERE p.nombre = :nombrePermiso")
    List<Usuario> findByPermiso(@Param("nombrePermiso") String nombrePermiso);
}