package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;

import java.time.LocalDate;
import java.util.List;

public interface IRegistMovimientService {
    List<EntradaProductoFX> obtenerTodos() throws Exception;
    List<EntradaProductoFX> obtenerPorIdProducto(Integer idProducto) throws Exception;
    List<EntradaProductoFX> obtenerPorProveedor(Integer idProveedor) throws Exception;
    List<EntradaProductoFX> obtenerPorFecha(LocalDate desde, LocalDate hasta) throws Exception;
    List<EntradaProductoFX> obtenerPorTipoMovimiento(String tipo) throws Exception;
    List<EntradaProductoFX> obtenerFiltrados(Integer idProducto, LocalDate desde, LocalDate hasta, String tipo) throws Exception;
    List<EntradaProductoFX> obtenerFiltradosCompleto(Integer idProducto, Integer idProveedor, LocalDate desde, LocalDate hasta, String tipo) throws Exception;
}