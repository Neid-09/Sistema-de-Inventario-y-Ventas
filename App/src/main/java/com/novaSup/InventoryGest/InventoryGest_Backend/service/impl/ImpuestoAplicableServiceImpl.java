package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ImpuestoAplicableRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.ImpuestoAplicableService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ImpuestoAplicableServiceImpl implements ImpuestoAplicableService {

    private final ImpuestoAplicableRepository impuestoAplicableRepository;

    public ImpuestoAplicableServiceImpl(ImpuestoAplicableRepository impuestoAplicableRepository) {
        this.impuestoAplicableRepository = impuestoAplicableRepository;
    }

    @Override
    public List<ImpuestoAplicable> obtenerImpuestosAplicables(Integer idProducto, Integer idCategoria, Date fechaActual) {
        if (idProducto == null && idCategoria == null) {
            throw new IllegalArgumentException("Se debe proporcionar al menos un idProducto o idCategoria.");
        }
        if (fechaActual == null) {
            throw new IllegalArgumentException("La fechaActual no puede ser nula.");
        }
        return impuestoAplicableRepository.findImpuestosAplicables(idProducto, idCategoria, fechaActual);
    }
    
    @Override
    public List<ImpuestoAplicable> findAll() {
        return impuestoAplicableRepository.findAll();
    }
    
    @Override
    public Optional<ImpuestoAplicable> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }
        return impuestoAplicableRepository.findById(id);
    }
    
    @Override
    public ImpuestoAplicable save(ImpuestoAplicable impuestoAplicable) {
        if (impuestoAplicable == null) {
            throw new IllegalArgumentException("El impuesto aplicable no puede ser nulo.");
        }
        return impuestoAplicableRepository.save(impuestoAplicable);
    }
    
    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }
        impuestoAplicableRepository.deleteById(id);
    }
} 