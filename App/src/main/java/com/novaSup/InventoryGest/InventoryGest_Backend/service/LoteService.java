package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import java.util.List;
import java.util.Optional;

public interface LoteService {
    List<Lote> obtenerTodos();
    Optional<Lote> obtenerPorId(Integer id);
    List<Lote> obtenerPorProducto(Integer idProducto);
    Lote guardar(Lote lote);
    void eliminar(Integer id);
    List<Lote> obtenerLotesProximosVencer(Integer diasAlerta);
    Optional<Lote> activarLote(Integer id);
    List<Lote> obtenerLotesInactivos();
}