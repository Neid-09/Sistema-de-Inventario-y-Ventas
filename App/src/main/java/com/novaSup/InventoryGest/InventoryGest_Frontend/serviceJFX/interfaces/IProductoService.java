package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;

import java.util.List;

public interface IProductoService {
    List<ProductoFX> obtenerTodos() throws Exception;
    List<ProductoFX> obtenerActivos() throws Exception;
    ProductoFX obtenerPorId(Integer id) throws Exception;
    ProductoFX obtenerPorCodigo(String codigo) throws Exception; // Nuevo
    List<ProductoFX> obtenerPorCategoria(Integer idCategoria) throws Exception; // Nuevo
    List<ProductoFX> obtenerConStockBajo() throws Exception; // Nuevo
    List<ProductoFX> obtenerConSobrestock() throws Exception; // Nuevo
    ProductoFX obtenerDetallesProducto(Integer id) throws Exception; // Nuevo
    List<ProductoFX> filtrarProductos(String nombre, String codigo, Integer idCategoria, Boolean estado) throws Exception;

    // CRUD básico
    ProductoFX guardar(ProductoFX producto) throws Exception;
    ProductoFX actualizar(Integer id, ProductoFX producto) throws Exception; // Modificado para incluir id
    void eliminar(Integer id) throws Exception;

    // Gestión de stock
    ProductoFX ajustarStock(Integer id, Integer cantidad) throws Exception; // Renombrado de actualizarStock

    // Gestión de estado
    ProductoFX cambiarEstado(Integer id, Boolean estado) throws Exception;
    ProductoFX desactivarProducto(Integer id) throws Exception; // Nuevo
    ProductoFX activarProducto(Integer id) throws Exception; // Nuevo

    // Verificaciones
    boolean existeCodigo(String codigo, Integer idProducto) throws Exception; // Modificado para quitar idProducto

    // Catálogos relacionados
    List<CategoriaFX> obtenerCategorias() throws Exception;
    List<ProveedorFX> obtenerProveedores() throws Exception;
}