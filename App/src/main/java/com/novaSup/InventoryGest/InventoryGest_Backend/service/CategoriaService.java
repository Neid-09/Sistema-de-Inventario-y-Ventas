package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> obtenerTodas();
    Optional<Categoria> obtenerPorId(Integer id);
    Categoria guardar(Categoria categoria);
    void eliminar(Integer id);
    List<Categoria> buscarPorNombre(String nombre);
}