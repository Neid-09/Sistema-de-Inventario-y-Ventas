package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import java.math.BigDecimal;
import java.util.List;

public interface IProductoService {
    List<ProductoFX> obtenerTodos() throws Exception;
    ProductoFX obtenerPorId(Integer id) throws Exception;
    ProductoFX guardar(String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception;
    ProductoFX actualizar(Integer id, String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception;
    void eliminar(Integer id) throws Exception;
    ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception;
    void desactivarProducto(Integer id) throws Exception;
}