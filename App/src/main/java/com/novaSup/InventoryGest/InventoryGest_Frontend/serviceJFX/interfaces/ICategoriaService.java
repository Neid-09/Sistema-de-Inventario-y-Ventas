package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {

    // Obtener todas las categorías
    List<CategoriaFX> obtenerTodas() throws Exception;

    // Buscar categorías por nombre
    List<CategoriaFX> buscarPorNombre(String nombre) throws Exception;

    // Obtener categoría por ID
    Optional<CategoriaFX> obtenerPorId(Integer id) throws Exception;

    // Guardar categoría (crear nueva o actualizar existente)
    CategoriaFX guardar(CategoriaFX categoria) throws Exception;

    // Eliminar categoría
    boolean eliminar(Integer id) throws Exception;

    // Obtener cantidad de productos asociados a una categoría
    int obtenerCantidadProductos(Integer idCategoria) throws Exception;
}