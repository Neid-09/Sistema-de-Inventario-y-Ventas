package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TipoImpuesto;
import java.util.List;
import java.util.Optional;

public interface TipoImpuestoService {
    List<TipoImpuesto> findAll();
    Optional<TipoImpuesto> findById(Integer id);
    TipoImpuesto save(TipoImpuesto tipoImpuesto);
    // Otros métodos si son necesarios
} 