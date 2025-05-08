package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFiscalesEmisorDTO {
    private String razonSocial;
    private String rfc;
    private String domicilio;
    private String regimenFiscal;
} 