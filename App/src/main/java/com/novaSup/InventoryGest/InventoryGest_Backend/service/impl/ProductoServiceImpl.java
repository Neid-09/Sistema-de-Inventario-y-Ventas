package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EntradaProductoService entradaProductoService;

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("El producto con ID " + id + " no existe.");
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Producto> actualizarStock(Integer id, Integer cantidad) {
        return productoRepository.findById(id).map(producto -> {
            int nuevoStock = producto.getStock() + cantidad;
            if (nuevoStock < 0) {
                throw new RuntimeException("El stock no puede ser negativo.");
            }
            producto.setStock(nuevoStock);
            Producto productoActualizado = productoRepository.save(producto);

            EntradaProducto entrada = new EntradaProducto();
            entrada.setProducto(producto);
            entrada.setCantidad(cantidad);
            entradaProductoService.guardar(entrada);

            return productoActualizado;
        }).or(() -> {
            throw new RuntimeException("Producto con ID " + id + " no encontrado.");
        });
    }

    @Override
    public Optional<Producto> desactivarProducto(Integer id) {
        return productoRepository.findById(id).map(producto -> {
            producto.setEstado(false);
            return productoRepository.save(producto);
        }).or(() -> {
            throw new RuntimeException("Producto con ID " + id + " no encontrado.");
        });
    }
}