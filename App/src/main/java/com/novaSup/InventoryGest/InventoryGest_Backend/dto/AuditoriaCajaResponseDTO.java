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
public class AuditoriaCajaResponseDTO {
    private Integer idAuditoria;
    private CajaResponseDTO caja; // Usar el DTO simplificado de Caja
    private BigDecimal dineroEsperado;
    private BigDecimal dineroReal;
    private LocalDateTime fecha;
    private String motivo;
    private UsuarioSimplifiedDTO usuario; // Usar el DTO simplificado de Usuario
} 