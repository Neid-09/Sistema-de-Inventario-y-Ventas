package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFiscalesReceptorPdfDTO {
    private String nombreRazonSocial;
    private String identificacion;
    private String direccion;
    private String usoFiscal; // Ej: P01 (Por definir), S01 (Sin efectos fiscales)
} 