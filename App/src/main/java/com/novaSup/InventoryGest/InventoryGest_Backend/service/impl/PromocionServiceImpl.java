package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Promocion;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PromocionRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.PromocionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @Override
    public List<Promocion> obtenerTodas() {
        return promocionRepository.findAll();
    }

    @Override
    public Optional<Promocion> obtenerPorId(Integer id) {
        return promocionRepository.findById(id);
    }

    @Override
    public Promocion guardar(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Override
    public void eliminar(Integer id) {
        promocionRepository.deleteById(id);
    }

    @Override
    public List<Promocion> obtenerPorProducto(Integer idProducto) {
        return promocionRepository.findByIdProducto(idProducto);
    }

    @Override
    public List<Promocion> obtenerPorCategoria(Integer idCategoria) {
        return promocionRepository.findByIdCategoria(idCategoria);
    }

    @Override
    public List<Promocion> obtenerPromocionesActivas() {
        return promocionRepository.findPromocionesActivas(LocalDate.now());
    }

    @Override
    public List<Promocion> obtenerPromocionesActivasPorProducto(Integer idProducto) {
        return promocionRepository.findPromocionesActivasPorProducto(idProducto, LocalDate.now());
    }
}