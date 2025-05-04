package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.CategoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @Override
    public Optional<Categoria> obtenerPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public void eliminar(Integer id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public List<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }
}