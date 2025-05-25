package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarMovimientoManualRequestDTO {
    private Integer idCaja;
    private String tipoMovimiento;
    private String descripcion;
    private BigDecimal monto;
    private Integer idUsuario;
} 