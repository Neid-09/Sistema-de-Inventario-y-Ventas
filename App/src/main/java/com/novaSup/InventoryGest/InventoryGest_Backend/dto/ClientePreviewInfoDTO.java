package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientePreviewInfoDTO {
    private int idCliente; // Cambiado a int
    private String nombre;
    private String razonSocial;
    private String identificacionFiscal;
    private String direccion;
    private String direccionFacturacion;
    // Añade otros campos del cliente necesarios para la factura

    // Getters y Setters generados por Lombok
} 