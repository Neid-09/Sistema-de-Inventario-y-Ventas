package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TipoImpuesto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.TipoImpuestoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.TipoImpuestoService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TipoImpuestoServiceImpl implements TipoImpuestoService {

    private final TipoImpuestoRepository tipoImpuestoRepository;

    public TipoImpuestoServiceImpl(TipoImpuestoRepository tipoImpuestoRepository) {
        this.tipoImpuestoRepository = tipoImpuestoRepository;
    }

    @Override
    public List<TipoImpuesto> findAll() {
        return tipoImpuestoRepository.findAll();
    }

    @Override
    public Optional<TipoImpuesto> findById(Integer id) {
        return tipoImpuestoRepository.findById(id);
    }

    @Override
    public TipoImpuesto save(TipoImpuesto tipoImpuesto) {
        return tipoImpuestoRepository.save(tipoImpuesto);
    }
} 