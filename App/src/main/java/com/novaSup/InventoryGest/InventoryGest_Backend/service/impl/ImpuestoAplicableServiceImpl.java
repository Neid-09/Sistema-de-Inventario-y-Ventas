package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ImpuestoAplicableRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ImpuestoAplicableService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

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
} 