package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Promocion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromocionService {
    List<Promocion> obtenerTodas();
    Optional<Promocion> obtenerPorId(Integer id);
    Promocion guardar(Promocion promocion);
    void eliminar(Integer id);
    List<Promocion> obtenerPorProducto(Integer idProducto);
    List<Promocion> obtenerPorCategoria(Integer idCategoria);
    List<Promocion> obtenerPromocionesActivas();
    List<Promocion> obtenerPromocionesActivasPorProducto(Integer idProducto);
    Optional<Promocion> buscarPromocionAplicable(Integer idProducto, Integer idCategoria, LocalDate fechaActual);
    BigDecimal aplicarDescuento(BigDecimal precioOriginal, Promocion promocion);
}