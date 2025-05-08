package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesEmisorDTO;

public interface ConfiguracionEmpresaService {
    /**
     * Obtiene los datos fiscales principales de la empresa emisora.
     * @return DTO con los datos fiscales del emisor.
     * @throws RuntimeException si no se encuentra la configuración de la empresa.
     */
    DatosFiscalesEmisorDTO obtenerDatosFiscalesEmisor();

    /**
     * Obtiene el RFC genérico para ser utilizado en facturas a "público en general".
     * @return El RFC genérico.
     * @throws RuntimeException si no se encuentra la configuración de la empresa o el RFC genérico.
     */
    String obtenerRfcPublicoGeneral();
} 