package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.CategoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProveedorRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private EntradaProductoService entradaProductoService;

    @Autowired
    private NotificacionServiceImpl notificacionService;

    @Autowired
    private PromocionService promocionService;

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> obtenerActivos() {
        return productoRepository.findByEstadoTrue();
    }

    @Override
    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    @Override
    public List<Producto> obtenerPorCategoria(Integer idCategoria) {
        return productoRepository.findByCategoria_IdCategoria(idCategoria);
    }

    @Override
    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        // Validaciones
        if (producto.getPrecioCosto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de costo debe ser positivo");
        }

        if (producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser positivo");
        }

        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

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

        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> buscarPorFiltros(String nombre, String codigo, Integer idCategoria, Boolean estado) {
        return productoRepository.buscarPorFiltros(nombre, codigo, idCategoria, estado);
    }

    @Override
    public boolean existsCodigo(String codigo) {
        return productoRepository.existsByCodigo(codigo);
    }

    @Override
    public List<Producto> obtenerConSobrestock() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockMaximo() != null && p.getStock() > p.getStockMaximo())
                .collect(Collectors.toList());
    }

    @Override
    public boolean tieneMovimientosAsociados(Integer idProducto) {
        // Verificar en ventas, entradas, etc.
        return entradaProductoService.existsEntradaByProductoId(idProducto) ||
                // Aquí agregarías más validaciones cuando tengas servicios de ventas, etc.
                false;
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
        return obtenerPorId(idProducto)
                .map(producto -> {
                    producto.setStock(nuevoStock);
                    Producto productoActualizado = guardar(producto);

                    // Verificar stock mínimo y máximo para generar notificaciones automáticas
                    if (producto.getStockMinimo() != null && nuevoStock <= producto.getStockMinimo()) {
                        // Pasar el objeto producto completo
                        notificacionService.notificarStockBajo(productoActualizado);
                    }

                    if (producto.getStockMaximo() != null && nuevoStock > producto.getStockMaximo()) {
                        // Pasar el objeto producto completo
                        notificacionService.notificarSobrestock(productoActualizado);
                    }

                    return productoActualizado;
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
        return productoRepository.findByProveedor_IdProveedor(idProveedor);
    }



    public Map<String, Object> obtenerDetallesProducto(Integer idProducto) {
        Map<String, Object> detalles = new HashMap<>();

        // Obtener el producto
        Optional<Producto> producto = obtenerPorId(idProducto);
        if (!producto.isPresent()) {
            return detalles;
        }

        detalles.put("producto", producto.get());

        // Añadir promociones activas
        List<Promocion> promociones = promocionService.obtenerPromocionesActivasPorProducto(idProducto);
        detalles.put("promociones", promociones);

        // Resto de lógica existente...

        return detalles;
    }
}