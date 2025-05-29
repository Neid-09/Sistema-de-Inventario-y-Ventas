package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> { // Cambiado a ClienteRepository y extiende JpaRepository

    // Método para buscar un cliente por su cédula (asumiendo que es única)
    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);

    // Método para buscar un cliente por su correo electrónico (asumiendo que es único)
    Optional<Cliente> findByCorreo(String correo);

    // Método para buscar un cliente por su nombre (devuelve Optional)
    Optional<Cliente> findByNombre(String nombre);

    // Método para buscar un cliente por su Identificacion Fiscal
    Optional<Cliente> findByIdentificacionFiscal(String identificacionFiscal);

    // Método para buscar clientes por su estado (activo/inactivo)
    List<Cliente> findByActivo(boolean activo);

    // Método para buscar un cliente por su número de celular
    Optional<Cliente> findByCelular(String celular);

    // Puedes añadir más métodos de consulta personalizados aquí si los necesitas
    // Por ejemplo, buscar clientes por nombre, etc.
}
