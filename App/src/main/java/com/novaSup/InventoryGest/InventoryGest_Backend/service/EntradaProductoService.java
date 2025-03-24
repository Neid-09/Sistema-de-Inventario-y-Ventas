package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import java.util.List;
import java.util.Optional;

public interface EntradaProductoService {
    List<EntradaProducto> obtenerTodas();
    List<EntradaProducto> obtenerPorProducto(Integer idProducto);
    EntradaProducto guardar(EntradaProducto entradaProducto);
}