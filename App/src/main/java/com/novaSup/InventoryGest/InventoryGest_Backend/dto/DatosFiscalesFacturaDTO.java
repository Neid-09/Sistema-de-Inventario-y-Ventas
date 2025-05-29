package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFiscalesFacturaDTO {
    private DatosFiscalesEmisorDTO emisor;
    private DatosFiscalesReceptorDTO receptor;
} 