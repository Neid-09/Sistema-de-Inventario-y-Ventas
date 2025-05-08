package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFiscalesReceptorDTO {
    private String razonSocial;
    private String rfc;
    private String domicilio;
    private String usoFactura; // Ejemplo: G01 Adquisición de mercancías, P01 Por definir
} 