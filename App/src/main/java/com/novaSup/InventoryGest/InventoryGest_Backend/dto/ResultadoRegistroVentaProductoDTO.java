package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoRegistroVentaProductoDTO {
    private RegistMovimient movimiento;
    private List<LoteReducidoInfoDTO> lotesAfectados;
} 