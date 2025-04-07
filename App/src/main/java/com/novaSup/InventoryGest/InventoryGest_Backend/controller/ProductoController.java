package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
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
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private LoteService loteService;

    @Autowired
    private AuditoriaService auditoriaService;

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
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        try {
            // Validaciones del producto
            if (producto.getCodigo() == null || producto.getCodigo().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El código del producto es obligatorio"));
            }

            if (productoService.existsCodigo(producto.getCodigo())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe un producto con ese código"));
            }

            // Guardar el producto
            Producto nuevoProducto = productoService.guardar(producto);

            // Registrar en auditoría
            auditoriaService.registrarAccion(
                    "CREAR",
                    "productos",
                    nuevoProducto.getIdProducto(),
                    null,
                    nuevoProducto,
                    null // El ID de usuario se obtiene del contexto de seguridad
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al crear el producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        return productoService.obtenerPorId(id)
                .map(productoExistente -> {
                    // Crear copia de los datos anteriores para auditoría
                    Producto datosAnteriores = new Producto();
                    // Copiar los valores importantes
                    datosAnteriores.setIdProducto(productoExistente.getIdProducto());
                    datosAnteriores.setCodigo(productoExistente.getCodigo());
                    datosAnteriores.setNombre(productoExistente.getNombre());
                    datosAnteriores.setPrecioCosto(productoExistente.getPrecioCosto());
                    datosAnteriores.setPrecioVenta(productoExistente.getPrecioVenta());
                    datosAnteriores.setStock(productoExistente.getStock());
                    datosAnteriores.setEstado(productoExistente.getEstado());

                    // Actualizar el producto
                    producto.setIdProducto(id);
                    Producto productoActualizado = productoService.guardar(producto);

                    // Registrar en auditoría
                    auditoriaService.registrarAccion(
                            "ACTUALIZAR",
                            "productos",
                            id,
                            datosAnteriores,
                            productoActualizado,
                            null
                    );

                    return ResponseEntity.ok(productoActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('eliminar_producto')")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id)
                .map(producto -> {
                    // Verificar si tiene movimientos asociados
                    if (productoService.tieneMovimientosAsociados(id)) {
                        // En vez de eliminar, cambiar estado a inactivo
                        Producto productoAnterior = new Producto();
                        productoAnterior.setIdProducto(producto.getIdProducto());
                        productoAnterior.setEstado(producto.getEstado());

                        producto.setEstado(false);
                        Producto productoDesactivado = productoService.guardar(producto);

                        // Auditar cambio de estado
                        auditoriaService.registrarAccion(
                                "DESACTIVAR",
                                "productos",
                                id,
                                productoAnterior,
                                productoDesactivado,
                                null
                        );

                        return ResponseEntity.ok().body(Map.of(
                                "mensaje", "El producto tiene movimientos asociados. Se ha desactivado en lugar de eliminar.",
                                "desactivado", true
                        ));
                    } else {
                        // Guardar datos para auditoría antes de eliminar
                        Producto productoEliminado = new Producto();
                        productoEliminado.setIdProducto(producto.getIdProducto());
                        productoEliminado.setCodigo(producto.getCodigo());
                        productoEliminado.setNombre(producto.getNombre());

                        productoService.eliminar(id);

                        // Auditar eliminación
                        auditoriaService.registrarAccion(
                                "ELIMINAR",
                                "productos",
                                id,
                                productoEliminado,
                                null,
                                null
                        );

                        return ResponseEntity.ok().build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Establecer stock directamente (método actual)
    @PatchMapping("/{id}/stock/establecer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<?> establecerStock(@PathVariable Integer id, @RequestParam Integer cantidad) {
        if (cantidad < 0) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El stock no puede ser negativo"));
        }

        return productoService.obtenerPorId(id)
                .map(producto -> {
                    Integer stockOriginal = producto.getStock();
                    Producto productoAnterior = new Producto();
                    productoAnterior.setIdProducto(producto.getIdProducto());
                    productoAnterior.setStock(stockOriginal);

                    Producto actualizado = productoService.actualizarStock(id, cantidad).orElse(null);

                    if (actualizado != null) {
                        auditoriaService.registrarAccion(
                                "ESTABLECER_STOCK",
                                "productos",
                                id,
                                productoAnterior,
                                actualizado,
                                null
                        );
                        return ResponseEntity.ok(actualizado);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Incrementar o decrementar stock
    @PatchMapping("/{id}/stock/ajustar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_producto')")
    public ResponseEntity<?> ajustarStock(@PathVariable Integer id, @RequestParam Integer cantidad) {
        return productoService.obtenerPorId(id)
                .map(producto -> {
                    Integer stockOriginal = producto.getStock();
                    Integer nuevoStock = stockOriginal + cantidad;

                    if (nuevoStock < 0) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "mensaje", "El stock resultante sería negativo",
                                "stockActual", stockOriginal,
                                "ajusteSolicitado", cantidad
                        ));
                    }

                    Producto productoAnterior = new Producto();
                    productoAnterior.setIdProducto(producto.getIdProducto());
                    productoAnterior.setStock(stockOriginal);

                    Producto actualizado = productoService.actualizarStock(id, nuevoStock).orElse(null);

                    if (actualizado != null) {
                        auditoriaService.registrarAccion(
                                cantidad >= 0 ? "INCREMENTAR_STOCK" : "DECREMENTAR_STOCK",
                                "productos",
                                id,
                                productoAnterior,
                                actualizado,
                                null
                        );
                        return ResponseEntity.ok(actualizado);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
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

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<?> obtenerDetallesProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id)
                .map(producto -> {
                    Map<String, Object> detalles = new HashMap<>();
                    detalles.put("producto", producto);

                    // Obtener lotes asociados
                    List<Lote> lotes = loteService.obtenerPorProducto(id);
                    detalles.put("lotes", lotes);

                    // Indicadores de stock
                    boolean bajoStock = producto.getStockMinimo() != null &&
                            producto.getStock() <= producto.getStockMinimo();

                    boolean sobreStock = producto.getStockMaximo() != null &&
                            producto.getStock() > producto.getStockMaximo();

                    detalles.put("indicadores", Map.of(
                            "bajoStock", bajoStock,
                            "sobreStock", sobreStock
                    ));

                    return ResponseEntity.ok(detalles);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}