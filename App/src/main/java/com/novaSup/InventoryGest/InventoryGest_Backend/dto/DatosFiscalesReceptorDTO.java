package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFiscalesReceptorDTO {
    private String razonSocial;
    private String identificacionFiscal;
    private String direccionFacturacion;
    private String tipoFactura; // Ejemplo: G01 Adquisición de mercancías, P01 Por definir
} 