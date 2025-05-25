package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CerrarCajaRequestDTO {
    private BigDecimal dineroReal;
    // El idCaja se obtiene del path variable, no del body
} 