package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TasaImpuesto;
import java.util.List;
import java.util.Optional;

public interface TasaImpuestoService {
    List<TasaImpuesto> findAll();
    Optional<TasaImpuesto> findById(Integer id);
    TasaImpuesto save(TasaImpuesto tasaImpuesto);
    // Otros métodos si son necesarios
} 