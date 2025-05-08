package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
import java.util.Date;
import java.util.List;

public interface ImpuestoAplicableService {
    List<ImpuestoAplicable> obtenerImpuestosAplicables(Integer idProducto, Integer idCategoria, Date fechaActual);
    // Otros métodos CRUD básicos si se necesitan, como save, findById, etc.
} 