package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CajaResponseDTO {
    private Integer idCaja;
    private UsuarioSimplifiedDTO usuario;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private BigDecimal dineroInicial;
    private BigDecimal dineroTotal;
    private String estado;
} 