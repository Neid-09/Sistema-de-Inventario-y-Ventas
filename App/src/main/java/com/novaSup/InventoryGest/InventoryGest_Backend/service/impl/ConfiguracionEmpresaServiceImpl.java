package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesEmisorDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.ConfiguracionEmpresa;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ConfiguracionEmpresaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ConfiguracionEmpresaService;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracionEmpresaServiceImpl implements ConfiguracionEmpresaService {

    private final ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    public ConfiguracionEmpresaServiceImpl(ConfiguracionEmpresaRepository configuracionEmpresaRepository) {
        this.configuracionEmpresaRepository = configuracionEmpresaRepository;
    }

    @Override
    public DatosFiscalesEmisorDTO obtenerDatosFiscalesEmisor() {
        ConfiguracionEmpresa config = configuracionEmpresaRepository.findFirstByOrderByIdConfiguracionAsc()
                .orElseThrow(() -> new RuntimeException("Configuración de la empresa no encontrada."));

        // Construir el domicilio completo
        String domicilioCompleto = String.join(", ",
                config.getDomicilioCalle(),
                config.getDomicilioNumeroExterior(),
                config.getDomicilioBarrioColonia(),
                config.getDomicilioLocalidad(),
                config.getDomicilioMunicipio(),
                config.getDomicilioEstadoProvincia(),
                config.getDomicilioPais(),
                "C.P. " + config.getDomicilioCodigoPostal()
        ).replace(" ,", "").replace(", ,", ","); // Limpieza básica de comas extras

        return new DatosFiscalesEmisorDTO(
                config.getRazonSocialEmisor(),
                config.getIdentificacionFiscalEmisor(),
                domicilioCompleto,
                config.getRegimenFiscalEmisor()
        );
    }

    @Override
    public String obtenerRfcPublicoGeneral() {
        ConfiguracionEmpresa config = configuracionEmpresaRepository.findFirstByOrderByIdConfiguracionAsc()
                .orElseThrow(() -> new RuntimeException("Configuración de la empresa no encontrada."));
        if (config.getRfcPublicoGeneral() == null || config.getRfcPublicoGeneral().trim().isEmpty()) {
            throw new RuntimeException("RFC para público en general no configurado.");
        }
        return config.getRfcPublicoGeneral();
    }
} 