package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VendedorDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.mapper.VendedorMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.VendedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendedores")
@CrossOrigin(origins = "*")
public class VendedorController {

    private final VendedorService vendedorService;
    private final VendedorMapper vendedorMapper;

    public VendedorController(VendedorService vendedorService, VendedorMapper vendedorMapper) {
        this.vendedorService = vendedorService;
        this.vendedorMapper = vendedorMapper;
    }    /**
     * Obtener todos los vendedores
     */
    @GetMapping
    public ResponseEntity<List<VendedorDTO>> obtenerTodos() {
        try {
            List<Vendedor> vendedores = vendedorService.obtenerTodos();
            List<VendedorDTO> vendedoresDTO = vendedorMapper.toDTOList(vendedores);
            return ResponseEntity.ok(vendedoresDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener vendedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendedorDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Vendedor> vendedor = vendedorService.obtenerPorId(id);
            if (vendedor.isPresent()) {
                VendedorDTO vendedorDTO = vendedorMapper.toDTO(vendedor.get());
                return ResponseEntity.ok(vendedorDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crear un nuevo vendedor
     */
    @PostMapping
    public ResponseEntity<VendedorDTO> crearVendedor(@RequestBody VendedorDTO vendedorDTO) {
        try {
            if (vendedorDTO == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validaciones básicas
            if (vendedorDTO.getIdUsuario() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Vendedor vendedor = vendedorMapper.toEntity(vendedorDTO);
            Vendedor nuevoVendedor = vendedorService.guardar(vendedor);
            VendedorDTO nuevoVendedorDTO = vendedorMapper.toDTO(nuevoVendedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVendedorDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    /**
     * Actualizar un vendedor existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendedorDTO> actualizarVendedor(@PathVariable Integer id, @RequestBody VendedorDTO vendedorDTO) {
        try {
            Optional<Vendedor> vendedorExistente = vendedorService.obtenerPorId(id);
            if (!vendedorExistente.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Vendedor vendedor = vendedorMapper.toEntity(vendedorDTO);
            vendedor.setIdVendedor(id);
            Vendedor vendedorActualizado = vendedorService.guardar(vendedor);
            VendedorDTO vendedorActualizadoDTO = vendedorMapper.toDTO(vendedorActualizado);
            return ResponseEntity.ok(vendedorActualizadoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Eliminar un vendedor
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarVendedor(@PathVariable Integer id) {
        try {
            Optional<Vendedor> vendedor = vendedorService.obtenerPorId(id);
            if (!vendedor.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            vendedorService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener vendedores activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<VendedorDTO>> obtenerActivos() {
        try {
            List<Vendedor> vendedoresActivos = vendedorService.obtenerActivos();
            List<VendedorDTO> vendedoresActivosDTO = vendedorMapper.toDTOList(vendedoresActivos);
            return ResponseEntity.ok(vendedoresActivosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Desactivar un vendedor
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarVendedor(@PathVariable Integer id) {
        try {
            Optional<Vendedor> vendedor = vendedorService.obtenerPorId(id);
            if (!vendedor.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            vendedorService.desactivarVendedor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activar un vendedor
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarVendedor(@PathVariable Integer id) {
        try {
            Optional<Vendedor> vendedor = vendedorService.obtenerPorId(id);
            if (!vendedor.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            vendedorService.activarVendedor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    /**
     * Obtener vendedor con información de usuario por ID de usuario
     */
    @GetMapping("/{idUsuario}/con-usuario")
    public ResponseEntity<VendedorDTO> obtenerVendedorConUsuario(@PathVariable Integer idUsuario) {
        try {
            Optional<Vendedor> vendedor = vendedorService.obtenerVendedorPorIdUsuario(idUsuario);
            if (vendedor.isPresent()) {
                VendedorDTO vendedorDTO = vendedorMapper.toDTO(vendedor.get());
                return ResponseEntity.ok(vendedorDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
