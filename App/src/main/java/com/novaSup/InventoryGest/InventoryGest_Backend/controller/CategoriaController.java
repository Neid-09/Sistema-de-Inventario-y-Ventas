package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.CategoriaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_categorias')")
    public ResponseEntity<?> listarCategorias() {
        List<Categoria> categorias = categoriaService.obtenerTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_categorias')")
    public ResponseEntity<?> buscarCategoriasPorNombre(@RequestParam String nombre) {
        List<Categoria> categorias = categoriaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_categorias')")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Integer id) {
        return categoriaService.obtenerPorId(id)
                .map(categoria -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("categoria", categoria);

                    // Contar productos asociados a esta categoría
                    List<Producto> productosAsociados = productoService.obtenerPorCategoria(id);
                    response.put("cantidadProductos", productosAsociados.size());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria nuevaCategoria = categoriaService.guardar(categoria);

            // Auditoría
            auditoriaService.registrarAccion(
                    "CREAR",
                    "categorias",
                    nuevaCategoria.getIdCategoria(),
                    null,
                    nuevaCategoria,
                    null
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al crear la categoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Integer id,
            @RequestBody Categoria categoria) {

        return categoriaService.obtenerPorId(id)
                .map(categoriaExistente -> {
                    categoria.setIdCategoria(id);
                    Categoria categoriaActualizada = categoriaService.guardar(categoria);
                    return ResponseEntity.ok(categoriaActualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        return categoriaService.obtenerPorId(id)
                .map(categoria -> {
                    // Verificar si hay productos asociados a esta categoría
                    List<Producto> productosAsociados = productoService.obtenerPorCategoria(id);
                    if (!productosAsociados.isEmpty()) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("mensaje", "No se puede eliminar la categoría porque tiene productos asociados");
                        error.put("cantidadProductos", productosAsociados.size());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                    }

                    categoriaService.eliminar(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}