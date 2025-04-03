package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.CategoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

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
        if (producto.getIdCategoria() != null) {
            Optional<Categoria> categoria = categoriaRepository.findById(producto.getIdCategoria());
            categoria.ifPresent(producto::setCategoria);
        }
        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
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