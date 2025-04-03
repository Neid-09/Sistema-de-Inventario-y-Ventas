// ProveedorService.java
package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;
import java.util.List;
import java.util.Optional;

public interface ProveedorService {
    List<Proveedor> obtenerTodos();
    Optional<Proveedor> obtenerPorId(Integer id);
    Proveedor guardar(Proveedor proveedor);
    void eliminar(Integer id);
}