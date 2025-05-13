package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;

import java.util.List;

public interface IProductoService {
    List<ProductoFX> obtenerTodos() throws Exception;
    List<ProductoFX> obtenerActivos() throws Exception;
    ProductoFX obtenerPorId(Integer id) throws Exception;
    List<ProductoFX> filtrarProductos(String nombre, String codigo, Integer idCategoria, Boolean estado) throws Exception;

    // CRUD básico
    ProductoFX guardar(ProductoFX producto) throws Exception;
    ProductoFX actualizar(ProductoFX producto) throws Exception;
    void eliminar(Integer id) throws Exception;

    // Gestión de stock
    ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception;

    // Gestión de estado
    ProductoFX cambiarEstado(Integer id, Boolean estado) throws Exception;

    // Verificaciones
    boolean existeCodigo(String codigo, Integer idProducto) throws Exception;

    // Catálogos relacionados
    List<CategoriaFX> obtenerCategorias() throws Exception;
    List<ProveedorFX> obtenerProveedores() throws Exception;
}