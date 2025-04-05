package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Producto>> listarProductosActivos() {
        return ResponseEntity.ok(productoService.obtenerActivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<Producto> obtenerProductoPorCodigo(@PathVariable String codigo) {
        return productoService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{idCategoria}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Integer idCategoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(idCategoria));
    }

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Producto>> obtenerProductosConStockBajo() {
        return ResponseEntity.ok(productoService.obtenerConStockBajo());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('crear_producto')")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        return new ResponseEntity<>(productoService.guardar(producto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoService.obtenerPorId(id)
                .map(p -> {
                    producto.setIdProducto(id);
                    return ResponseEntity.ok(productoService.guardar(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('eliminar_producto')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id)
                .map(p -> {
                    productoService.eliminar(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Integer id, @RequestParam Integer cantidad) {
        return productoService.actualizarStock(id, cantidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<Producto> desactivarProducto(@PathVariable Integer id) {
        return productoService.desactivarProducto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<Producto> activarProducto(@PathVariable Integer id) {
        return productoService.activarProducto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Producto>> filtrarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(productoService.buscarPorFiltros(nombre, codigo, idCategoria, estado));
    }

    @GetMapping("/verificar-codigo/{codigo}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('crear_producto') or hasAuthority('editar_producto')")
    public ResponseEntity<Boolean> verificarCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.existsCodigo(codigo));
    }

    @GetMapping("/sobrestock")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Producto>> obtenerProductosConSobrestock() {
        return ResponseEntity.ok(productoService.obtenerConSobrestock());
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<Producto> cambiarEstado(@PathVariable Integer id, @RequestParam Boolean estado) {
        return productoService.obtenerPorId(id)
                .map(p -> {
                    p.setEstado(estado);
                    return ResponseEntity.ok(productoService.guardar(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}