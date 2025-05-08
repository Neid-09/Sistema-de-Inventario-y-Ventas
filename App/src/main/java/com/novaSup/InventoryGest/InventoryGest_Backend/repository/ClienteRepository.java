package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> { // Cambiado a ClienteRepository y extiende JpaRepository

    // Método para buscar un cliente por su cédula (asumiendo que es única)
    Optional<Cliente> findByCedula(String cedula);

    // Método para buscar un cliente por su correo electrónico (asumiendo que es único)
    Optional<Cliente> findByCorreo(String correo);

    // Método para buscar un cliente por su nombre
    Optional<Cliente> findByNombre(String nombre);

    // Puedes añadir más métodos de consulta personalizados aquí si los necesitas
    // Por ejemplo, buscar clientes por nombre, etc.
}
