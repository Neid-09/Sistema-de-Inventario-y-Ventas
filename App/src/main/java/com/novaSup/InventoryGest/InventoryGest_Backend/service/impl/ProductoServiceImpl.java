package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.CategoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProveedorRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        // Aquí implementarías la lógica para verificar si el producto
        // tiene ventas, entradas u otros movimientos asociados
        // Para simplificar, asumimos que existe un método en algún servicio
        return false; // Por ahora retornamos false
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
    @Transactional
    public Optional<Producto> actualizarStock(Integer id, Integer cantidad) {
        return productoRepository.findById(id).map(producto -> {
            // Actualizar stock
            int nuevoStock = producto.getStock() + cantidad;
            producto.setStock(nuevoStock);
            Producto productoActualizado = productoRepository.save(producto);

            // Registrar entrada/salida
            EntradaProducto entrada = new EntradaProducto();
            entrada.setProducto(producto);
            entrada.setCantidad(cantidad);
            entrada.setFecha(LocalDateTime.now());
            entrada.setTipoMovimiento(cantidad > 0 ? "ENTRADA" : "SALIDA");
            entrada.setPrecioUnitario(cantidad > 0 ? producto.getPrecioCosto() : producto.getPrecioVenta());
            entradaProductoService.guardar(entrada);

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
}