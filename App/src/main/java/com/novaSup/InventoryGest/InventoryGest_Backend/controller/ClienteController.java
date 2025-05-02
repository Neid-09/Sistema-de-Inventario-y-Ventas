package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/clientes") // Ruta base para las operaciones de clientes
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Obtener todos los clientes
    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.obtenerTodosLosClientes();
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Integer id) {
        return clienteService.obtenerClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

     // Obtener un cliente por Cédula
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<Cliente> getClienteByCedula(@PathVariable String cedula) {
        return clienteService.obtenerClientePorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Crear un nuevo cliente
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
         try {
            Cliente nuevoCliente = clienteService.guardarCliente(cliente);
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
             // Captura excepciones de validación (cédula/correo duplicado)
             return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Captura otras posibles excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el cliente: " + e.getMessage());
        }
    }

    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Integer id, @RequestBody Cliente clienteDetalles) {
       try {
            Cliente clienteActualizado = clienteService.actualizarCliente(id, clienteDetalles);
            return ResponseEntity.ok(clienteActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el cliente: " + e.getMessage());
        }
    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Integer id) {
         try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.noContent().build(); // 204 No Content es estándar para delete exitoso
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
             // Considerar si hay dependencias que impidan borrar (ConstraintViolationException)
             // Podrías devolver un HttpStatus.CONFLICT (409) o INTERNAL_SERVER_ERROR (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Evitar exponer detalles del error
        }
    }

     // Endpoint específico para añadir puntos (ejemplo, podría ser parte de otra lógica como ventas)
    @PostMapping("/{idCliente}/puntos")
    public ResponseEntity<?> anadirPuntos(@PathVariable Integer idCliente, @RequestParam int puntos) {
         if (puntos <= 0) {
            return ResponseEntity.badRequest().body("La cantidad de puntos a añadir debe ser positiva.");
        }
        try {
            Cliente clienteActualizado = clienteService.anadirPuntosFidelidad(idCliente, puntos);
            return ResponseEntity.ok(clienteActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir puntos: " + e.getMessage());
        }
    }

    // Nota: La actualización de 'totalComprado' y 'ultimaCompra' usualmente se haría
    // como parte de una transacción de VENTA, no directamente en el controlador de Cliente.
    // El método existe en el servicio por si se necesita en otros contextos.

}
