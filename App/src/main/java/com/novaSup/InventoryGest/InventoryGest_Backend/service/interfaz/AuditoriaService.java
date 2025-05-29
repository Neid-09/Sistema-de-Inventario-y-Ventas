package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Auditoria;

public interface AuditoriaService {
    Auditoria registrarAccion(String accion, String tablaAfectada, Integer idRegistro,
                              Object datosAnteriores, Object datosNuevos, Integer idUsuario);
}