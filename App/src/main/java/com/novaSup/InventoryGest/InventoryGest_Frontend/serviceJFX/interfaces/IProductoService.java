package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import java.math.BigDecimal;
import java.util.List;

public interface IProductoService {
    // Obtener todos los productos
    List<ProductoFX> obtenerTodos() throws Exception;

    // Obtener un producto por su ID
    ProductoFX obtenerPorId(Integer id) throws Exception;

    // Guardar un nuevo producto
    ProductoFX guardar(String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception;

    // Actualizar un producto existente
    ProductoFX actualizar(Integer id, String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception;

    // Eliminar un producto por su ID
    void eliminar(Integer id) throws Exception;

    // Actualizar el stock de un producto
    ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception;

    // Desactivar un producto (cambiar su estado a inactivo)
    void desactivarProducto(Integer id) throws Exception;

    // Actualizar un producto completo (usado en el controlador)
    void actualizarProducto(ProductoFX producto) throws Exception;
}