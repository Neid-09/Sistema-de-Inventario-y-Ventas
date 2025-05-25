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
public class MovimientoCajaResponseDTO {
    private Integer idMovimiento;
    private CajaResponseDTO caja; // Usar el DTO simplificado de Caja
    private String tipoMovimiento;
    private String descripcion;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private UsuarioSimplifiedDTO usuario; // Usar el DTO simplificado de Usuario
    // private VentaDTO venta; // Considerar un DTO para Venta si es necesario mostrar información simplificada
} 