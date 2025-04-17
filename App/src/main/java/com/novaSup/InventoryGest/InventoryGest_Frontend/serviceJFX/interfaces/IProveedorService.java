package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import java.util.List;
import java.util.Optional;

public interface IProveedorService {
    List<ProveedorFX> obtenerTodos() throws Exception;
    List<ProveedorFX> buscarPorNombreOCorreo(String termino) throws Exception;
    Optional<ProveedorFX> obtenerPorId(Integer id) throws Exception;
    ProveedorFX guardar(ProveedorFX proveedor) throws Exception;
    ProveedorFX actualizar(Integer id, ProveedorFX proveedor) throws Exception;
    boolean eliminar(Integer id) throws Exception;
    int obtenerCantidadProductosAsociados(Integer id) throws Exception;
}