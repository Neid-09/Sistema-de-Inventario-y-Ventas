package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.EntradaProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntradaProductoServiceImpl implements EntradaProductoService {

    @Autowired
    private EntradaProductoRepository entradaProductoRepository;

    @Override
    public List<EntradaProducto> obtenerTodas() {
        return entradaProductoRepository.findAll();
    }

    @Override
    public List<EntradaProducto> obtenerPorProducto(Integer idProducto) {
        return entradaProductoRepository.findByProductoIdProducto(idProducto);
    }

    @Override
    public EntradaProducto guardar(EntradaProducto entradaProducto) {
        return entradaProductoRepository.save(entradaProducto);
    }

    @Override
    public boolean existsEntradaByProductoId(Integer idProducto) {
        return !entradaProductoRepository.findByProductoIdProducto(idProducto).isEmpty();
    }
}