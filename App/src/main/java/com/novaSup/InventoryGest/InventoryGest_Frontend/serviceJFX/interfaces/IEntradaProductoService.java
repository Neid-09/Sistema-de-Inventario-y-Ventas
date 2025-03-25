package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IEntradaProductoService {
    List<EntradaProductoFX> obtenerTodos() throws Exception;
    List<EntradaProductoFX> obtenerPorIdProducto(Integer idProducto) throws Exception;
    List<EntradaProductoFX> obtenerPorFecha(LocalDate desde, LocalDate hasta) throws Exception;
    List<EntradaProductoFX> obtenerPorTipoMovimiento(TipoMovimiento tipo) throws Exception;
    List<EntradaProductoFX> obtenerFiltrados(Integer idProducto, LocalDate desde, LocalDate hasta, TipoMovimiento tipo) throws Exception;
    EntradaProductoFX registrarMovimiento(Integer idProducto, Integer cantidad, TipoMovimiento tipo, BigDecimal precioUnitario) throws Exception;
}