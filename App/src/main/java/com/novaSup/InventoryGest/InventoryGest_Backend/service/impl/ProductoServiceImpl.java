package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.CategoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProveedorRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private EntradaProductoService entradaProductoService;

    @Autowired
    private NotificacionServiceImpl notificacionService;

    @Autowired
    private PromocionService promocionService;

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = productoRepository.findAll();
        productos.forEach(this::actualizarStockDesdeLoterRepositorio);
        return productos;
    }

    @Override
    public List<Producto> obtenerActivos() {
        List<Producto> productos = productoRepository.findByEstadoTrue();
        productos.forEach(this::actualizarStockDesdeLoterRepositorio);
        return productos;
    }

    @Override
    public Optional<Producto> obtenerPorId(Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);
        producto.ifPresent(this::actualizarStockDesdeLoterRepositorio);
        return producto;
    }

    @Override
    public Optional<Producto> obtenerPorCodigo(String codigo) {
        Optional<Producto> producto = productoRepository.findByCodigo(codigo);
        producto.ifPresent(this::actualizarStockDesdeLoterRepositorio);
        return producto;
    }

    @Override
    public List<Producto> obtenerPorCategoria(Integer idCategoria) {
        List<Producto> productos = productoRepository.findByCategoria_IdCategoria(idCategoria);
        productos.forEach(this::actualizarStockDesdeLoterRepositorio);
        return productos;
    }

    @Override
    public List<Producto> obtenerConStockBajo() {
        // Primero actualizamos el stock de todos los productos
        List<Producto> todosProductos = productoRepository.findAll();
        todosProductos.forEach(this::actualizarStockDesdeLoterRepositorio);

        // Luego filtramos los que están por debajo del mínimo
        return todosProductos.stream()
                .filter(p -> p.getEstado() && p.getStockMinimo() != null && p.getStock() <= p.getStockMinimo())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        // Validaciones básicas
        if (producto.getPrecioCosto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de costo debe ser positivo");
        }

        if (producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser positivo");
        }

        // No validamos stock negativo ya que ahora se calculará desde los lotes

        // Validar código único para productos nuevos
        if (producto.getIdProducto() == null &&
                productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con ese código");
        }

        // Establecer categoría
        if (producto.getIdCategoria() != null) {
            Optional<Categoria> categoria = categoriaRepository.findById(producto.getIdCategoria());
            categoria.ifPresent(producto::setCategoria);
        }

        // Establecer proveedor
        if (producto.getIdProveedor() != null) {
            Optional<Proveedor> proveedor = proveedorRepository.findById(producto.getIdProveedor());
            proveedor.ifPresent(producto::setProveedor);
        }

        Producto guardado = productoRepository.save(producto);

        // Actualizamos el stock desde los lotes después de guardar
        actualizarStockDesdeLoterRepositorio(guardado);

        return guardado;
    }

    @Override
    public List<Producto> buscarPorFiltros(String nombre, String codigo, Integer idCategoria, Boolean estado) {
        List<Producto> productos = productoRepository.buscarPorFiltros(nombre, codigo, idCategoria, estado);
        productos.forEach(this::actualizarStockDesdeLoterRepositorio);
        return productos;
    }

    @Override
    public boolean existsCodigo(String codigo) {
        return productoRepository.existsByCodigo(codigo);
    }

    @Override
    public List<Producto> obtenerConSobrestock() {
        List<Producto> todosProductos = productoRepository.findAll();
        todosProductos.forEach(this::actualizarStockDesdeLoterRepositorio);

        return todosProductos.stream()
                .filter(p -> p.getStockMaximo() != null && p.getStock() > p.getStockMaximo())
                .collect(Collectors.toList());
    }

    @Override
    public boolean tieneMovimientosAsociados(Integer idProducto) {
        // Verificar en ventas, entradas, etc.
        return entradaProductoService.existsEntradaByProductoId(idProducto);
    }

    @Override
    public void eliminar(Integer id) {
        // Verificar si tiene movimientos asociados
        if (tieneMovimientosAsociados(id)) {
            // En lugar de eliminar, cambiar estado a inactivo
            obtenerPorId(id).ifPresent(p -> {
                p.setEstado(false);
                productoRepository.save(p);
            });
        } else {
            productoRepository.deleteById(id);
        }
    }

    @Override
    public Optional<Producto> actualizarStock(Integer idProducto, Integer nuevoStock) {
        // Este método ya no establece directamente el stock,
        // ahora lo calcula desde los lotes
        return obtenerPorId(idProducto)
                .map(producto -> {
                    actualizarStockDesdeLoterRepositorio(producto);

                    // Verificar stock mínimo y máximo para generar notificaciones
                    if (producto.getStockMinimo() != null && producto.getStock() <= producto.getStockMinimo()) {
                        notificacionService.notificarStockBajo(producto);
                    }

                    if (producto.getStockMaximo() != null && producto.getStock() > producto.getStockMaximo()) {
                        notificacionService.notificarSobrestock(producto);
                    }

                    return producto;
                });
    }

    @Override
    public Optional<Producto> desactivarProducto(Integer id) {
        return obtenerPorId(id).map(producto -> {
            producto.setEstado(false);
            return guardar(producto);
        });
    }

    @Override
    public Optional<Producto> activarProducto(Integer id) {
        return obtenerPorId(id).map(producto -> {
            producto.setEstado(true);
            return guardar(producto);
        });
    }

    @Override
    public List<Producto> obtenerPorProveedor(Integer idProveedor) {
        List<Producto> productos = productoRepository.findByProveedor_IdProveedor(idProveedor);
        productos.forEach(this::actualizarStockDesdeLoterRepositorio);
        return productos;
    }

    // Método para actualizar el stock desde los lotes
    private void actualizarStockDesdeLoterRepositorio(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) return;

        stockService.actualizarStockProducto(producto.getIdProducto());
    }

    public Map<String, Object> obtenerDetallesProducto(Integer idProducto) {
        Map<String, Object> detalles = new HashMap<>();

        Optional<Producto> productoOpt = obtenerPorId(idProducto);
        if (!productoOpt.isPresent()) {
            return detalles;
        }

        Producto producto = productoOpt.get();
        actualizarStockDesdeLoterRepositorio(producto);

        detalles.put("producto", producto);

        // Añadir promociones activas
        List<Promocion> promociones = promocionService.obtenerPromocionesActivasPorProducto(idProducto);
        detalles.put("promociones", promociones);

        // Usar stockService para obtener los lotes
        List<Lote> lotes = stockService.obtenerLotesActivosPorProducto(idProducto);
        detalles.put("lotes", lotes);

        return detalles;
    }
}