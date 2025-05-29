package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoteReducidoInfoDTO {
    private Lote lote;
    private int cantidadTomada;
} 