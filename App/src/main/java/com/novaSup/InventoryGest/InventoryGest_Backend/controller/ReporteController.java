package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin("*")
public class ReporteController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private LoteService loteService;

    @Autowired
    private EntradaProductoService entradaProductoService;

    @GetMapping("/inventario/resumen")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<?> obtenerResumenInventario() {
        List<Producto> productos = productoService.obtenerTodos();
        List<Categoria> categorias = categoriaService.obtenerTodas();

        // Calcular estadísticas
        int totalProductos = productos.size();
        int productosActivos = (int) productos.stream().filter(Producto::getEstado).count();
        int productosBajoStock = productoService.obtenerConStockBajo().size();
        int productosSobrestock = productoService.obtenerConSobrestock().size();

        // Calcular valor total del inventario
        BigDecimal valorTotalCosto = productos.stream()
                .map(p -> p.getPrecioCosto().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalVenta = productos.stream()
                .map(p -> p.getPrecioVenta().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalProductos", totalProductos);
        resumen.put("productosActivos", productosActivos);
        resumen.put("productosBajoStock", productosBajoStock);
        resumen.put("productosSobrestock", productosSobrestock);
        resumen.put("valorTotalCosto", valorTotalCosto);
        resumen.put("valorTotalVenta", valorTotalVenta);
        resumen.put("totalCategorias", categorias.size());

        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/inventario/por-categoria")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<?> obtenerInventarioPorCategoria() {
        List<Categoria> categorias = categoriaService.obtenerTodas();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Categoria categoria : categorias) {
            List<Producto> productosCategoria = productoService.obtenerPorCategoria(categoria.getIdCategoria());

            int cantidadProductos = productosCategoria.size();
            int stockTotal = productosCategoria.stream().mapToInt(Producto::getStock).sum();

            BigDecimal valorTotalCosto = productosCategoria.stream()
                    .map(p -> p.getPrecioCosto().multiply(BigDecimal.valueOf(p.getStock())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> datoCategoria = new HashMap<>();
            datoCategoria.put("idCategoria", categoria.getIdCategoria());
            datoCategoria.put("nombre", categoria.getNombre());
            datoCategoria.put("cantidadProductos", cantidadProductos);
            datoCategoria.put("stockTotal", stockTotal);
            datoCategoria.put("valorInventario", valorTotalCosto);

            resultado.add(datoCategoria);
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/productos/stock-critico")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<?> obtenerProductosStockCritico() {
        List<Producto> productosBajoStock = productoService.obtenerConStockBajo();

        List<Map<String, Object>> resultado = productosBajoStock.stream().map(p -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("idProducto", p.getIdProducto());
            datos.put("codigo", p.getCodigo());
            datos.put("nombre", p.getNombre());
            datos.put("stock", p.getStock());
            datos.put("stockMinimo", p.getStockMinimo());
            datos.put("diferencia", p.getStockMinimo() - p.getStock());

            return datos;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/lotes/proximos-vencer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<?> obtenerLotesProximosVencer(
            @RequestParam(defaultValue = "30") Integer diasAlerta) {
        List<Lote> lotesProximosVencer = loteService.obtenerLotesProximosVencer(diasAlerta);
        return ResponseEntity.ok(lotesProximosVencer);
    }

    @GetMapping("/movimientos/por-periodo")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<?> obtenerMovimientosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipoMovimiento) {

        List<EntradaProducto> entradas = entradaProductoService.obtenerTodas();

        // Filtrar por fechas
        entradas = entradas.stream()
                .filter(e -> !e.getFecha().toLocalDate().isBefore(desde) && !e.getFecha().toLocalDate().isAfter(hasta))
                .collect(Collectors.toList());

        // Filtrar por tipo si se especificó
        if (tipoMovimiento != null && !tipoMovimiento.isEmpty()) {
            entradas = entradas.stream()
                    .filter(e -> e.getTipoMovimiento().equalsIgnoreCase(tipoMovimiento))
                    .collect(Collectors.toList());
        }

        // Agrupar por fecha
        Map<LocalDate, List<EntradaProducto>> movimientosPorFecha = entradas.stream()
                .collect(Collectors.groupingBy(e -> e.getFecha().toLocalDate()));

        // Construir resultado
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("desde", desde);
        resultado.put("hasta", hasta);
        resultado.put("tipoMovimiento", tipoMovimiento);
        resultado.put("movimientos", movimientosPorFecha);
        resultado.put("totalMovimientos", entradas.size());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/productos/valoracion")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> obtenerValoracionInventario() {
        List<Producto> productos = productoService.obtenerActivos();

        List<Map<String, Object>> resultado = productos.stream().map(p -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("idProducto", p.getIdProducto());
            datos.put("codigo", p.getCodigo());
            datos.put("nombre", p.getNombre());
            datos.put("stock", p.getStock());
            datos.put("precioCosto", p.getPrecioCosto());
            datos.put("precioVenta", p.getPrecioVenta());
            datos.put("valorCosto", p.getPrecioCosto().multiply(BigDecimal.valueOf(p.getStock())));
            datos.put("valorVenta", p.getPrecioVenta().multiply(BigDecimal.valueOf(p.getStock())));
            datos.put("gananciaEstimada", p.getPrecioVenta().subtract(p.getPrecioCosto()).multiply(BigDecimal.valueOf(p.getStock())));

            return datos;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}