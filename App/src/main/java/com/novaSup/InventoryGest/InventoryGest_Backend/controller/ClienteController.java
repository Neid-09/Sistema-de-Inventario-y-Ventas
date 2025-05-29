package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes") // Ruta base para las operaciones de clientes
public class ClienteController {

    /* 
     * ALTER TABLE clientes
     * ADD limite_credito DECIMAL(10,2) DEFAULT 1000.00;
     * FIJAR ESO PARA CAMBIOS
     */

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

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

     // Obtener un cliente por Documento Identidad
    @GetMapping("/documento-identidad/{documentoIdentidad}")
    public ResponseEntity<Cliente> getClienteByDocumentoIdentidad(@PathVariable String documentoIdentidad) {
        return clienteService.obtenerClientePorDocumentoIdentidad(documentoIdentidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener un cliente por Nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Cliente> getClienteByNombre(@PathVariable String nombre) {
        return clienteService.obtenerClientePorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener un cliente por Identificacion Fiscal
    @GetMapping("/identificacion-fiscal/{identificacionFiscal}")
    public ResponseEntity<Cliente> getClienteByIdentificacionFiscal(@PathVariable String identificacionFiscal) {
        return clienteService.obtenerClientePorIdentificacionFiscal(identificacionFiscal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener clientes por estado (activo/inactivo)
    @GetMapping("/estado")
    public ResponseEntity<List<Cliente>> getClientesByEstado(@RequestParam boolean activo) {
        List<Cliente> clientes = clienteService.obtenerClientesPorEstado(activo);
        if (clientes.isEmpty()) {
            // Devolver una lista vacía con OK 200 es también una opción válida si no se encuentran
            // pero para consistencia con otros Optional, podemos devolver Not Found si la lista está vacía.
            // Opcionalmente: return ResponseEntity.ok(clientes);
            return ResponseEntity.notFound().build(); 
        }
        return ResponseEntity.ok(clientes);
    }

    // Obtener un cliente por número de celular
    @GetMapping("/celular/{celular}")
    public ResponseEntity<Cliente> getClienteByCelular(@PathVariable String celular) {
        return clienteService.obtenerClientePorCelular(celular)
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
