package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> obtenerTodos();
    Optional<Producto> obtenerPorId(Integer id);
    Producto guardar(Producto producto);
    void eliminar(Integer id);
    Optional<Producto> actualizarStock(Integer id, Integer cantidad);
    Optional<Producto> desactivarProducto(Integer id);
}