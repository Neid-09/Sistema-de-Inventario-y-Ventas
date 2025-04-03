package com.novaSup.InventoryGest.InventoryGest_Backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermisoVerificacionResponse {
    private String nombrePermiso;
    private boolean tienePermiso;
}