package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TasaImpuesto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.TasaImpuestoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.TasaImpuestoService;
// import org.springframework.beans.factory.annotation.Autowired; // Opcional con un solo constructor
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TasaImpuestoServiceImpl implements TasaImpuestoService {

    private final TasaImpuestoRepository tasaImpuestoRepository;

    // @Autowired // Opcional aquí
    public TasaImpuestoServiceImpl(TasaImpuestoRepository tasaImpuestoRepository) {
        this.tasaImpuestoRepository = tasaImpuestoRepository;
    }

    @Override
    public List<TasaImpuesto> findAll() {
        return tasaImpuestoRepository.findAll();
    }

    @Override
    public Optional<TasaImpuesto> findById(Integer id) {
        return tasaImpuestoRepository.findById(id);
    }

    @Override
    public TasaImpuesto save(TasaImpuesto tasaImpuesto) {
        return tasaImpuestoRepository.save(tasaImpuesto);
    }
} 