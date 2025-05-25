package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AbrirCajaRequestDTO {
    private Integer idUsuario;
    private BigDecimal dineroInicial;
    private Boolean heredarSaldoAnterior; // Usar Boolean para permitir null si no se envía
    private String justificacionManual;
} 