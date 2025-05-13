package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaLoteUsoResponseDTO {
    private Integer idLote; // ID del lote
    private String codigoLote; // Código o identificador legible del lote
    private Integer cantidadTomada;
} 