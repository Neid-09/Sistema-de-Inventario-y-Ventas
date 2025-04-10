package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LoteService {
    List<Lote> obtenerTodos();
    Optional<Lote> obtenerPorId(Integer id);
    List<Lote> obtenerPorProducto(Integer idProducto);
    Lote guardar(Lote lote);
    void eliminar(Integer id);
    List<Lote> obtenerLotesProximosVencer(Integer diasAlerta);
    List<Lote> obtenerLotesPorRangoFechaEntrada(Date fechaInicio, Date fechaFin);
    Optional<Lote> activarLote(Integer id);
    Optional<Lote> desactivarLote(Integer id);
    List<Lote> obtenerLotesInactivos();
    Lote reducirCantidadLote(Integer idLote, Integer cantidad) throws Exception;
    void reducirCantidadDeLotes(Integer idProducto, Integer cantidadTotal) throws Exception;
    Lote procesarDevolucion(Integer idLote, Integer cantidad) throws Exception;
    List<Lote> obtenerLotesActivos();
}