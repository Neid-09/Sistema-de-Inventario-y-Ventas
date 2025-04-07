package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_proveedores')")
    public ResponseEntity<?> listarProveedores() {
        List<Proveedor> proveedores = proveedorService.obtenerTodos();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_proveedores')")
    public ResponseEntity<?> buscarProveedores(@RequestParam String termino) {
        List<Proveedor> proveedores = proveedorService.buscarPorNombreOCorreo(termino);
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_proveedores')")
    public ResponseEntity<?> obtenerProveedor(@PathVariable Integer id) {
        return proveedorService.obtenerPorId(id)
                .map(proveedor -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("proveedor", proveedor);

                    // Contar productos asociados
                    List<Producto> productosAsociados = productoService.obtenerPorProveedor(id);
                    response.put("cantidadProductos", productosAsociados.size());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_proveedores')")
    public ResponseEntity<?> crearProveedor(@RequestBody Proveedor proveedor) {
        try {
            // Validar formato de correo
            if (proveedor.getCorreo() != null && !proveedor.getCorreo().isEmpty()) {
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                Pattern pattern = Pattern.compile(emailRegex);
                if (!pattern.matcher(proveedor.getCorreo()).matches()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("mensaje", "El formato del correo electrónico no es válido"));
                }
            }

            Proveedor nuevoProveedor = proveedorService.guardar(proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al crear el proveedor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_proveedores')")
    public ResponseEntity<?> actualizarProveedor(
            @PathVariable Integer id,
            @RequestBody Proveedor proveedor) {

        return proveedorService.obtenerPorId(id)
                .map(proveedorExistente -> {
                    // Validar formato de correo
                    if (proveedor.getCorreo() != null && !proveedor.getCorreo().isEmpty()) {
                        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                        Pattern pattern = Pattern.compile(emailRegex);
                        if (!pattern.matcher(proveedor.getCorreo()).matches()) {
                            return ResponseEntity.badRequest().body(
                                    Map.of("mensaje", "El formato del correo electrónico no es válido"));
                        }
                    }

                    proveedor.setIdProveedor(id);
                    Proveedor proveedorActualizado = proveedorService.guardar(proveedor);
                    return ResponseEntity.ok(proveedorActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_proveedores')")
    public ResponseEntity<?> eliminarProveedor(@PathVariable Integer id) {
        return proveedorService.obtenerPorId(id)
                .map(proveedor -> {
                    // Verificar si hay productos asociados
                    List<Producto> productosAsociados = productoService.obtenerPorProveedor(id);
                    if (!productosAsociados.isEmpty()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("mensaje", "No se puede eliminar el proveedor porque tiene productos asociados");
                        error.put("cantidadProductos", productosAsociados.size());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                    }

                    proveedorService.eliminar(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}