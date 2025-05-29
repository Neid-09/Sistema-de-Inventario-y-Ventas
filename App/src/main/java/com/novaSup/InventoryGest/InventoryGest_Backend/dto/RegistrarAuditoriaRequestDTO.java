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
public class RegistrarAuditoriaRequestDTO {
    private Integer idCaja;
    private BigDecimal dineroEsperado;
    private BigDecimal dineroReal;
    private String motivo;
    private Integer idUsuario;
} 