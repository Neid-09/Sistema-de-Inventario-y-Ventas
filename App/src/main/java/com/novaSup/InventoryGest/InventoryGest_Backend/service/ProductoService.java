package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> obtenerTodos();
    List<Producto> obtenerActivos();
    Optional<Producto> obtenerPorId(Integer id);
    Optional<Producto> obtenerPorCodigo(String codigo);
    List<Producto> obtenerPorCategoria(Integer idCategoria);
    List<Producto> obtenerConStockBajo();
    Producto guardar(Producto producto);
    void eliminar(Integer id);
    Optional<Producto> actualizarStock(Integer id, Integer cantidad);
    Optional<Producto> desactivarProducto(Integer id);
    Optional<Producto> activarProducto(Integer id);
}