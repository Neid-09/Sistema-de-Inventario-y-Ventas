package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Configuracion;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ConfiguracionRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.ConfiguracionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfiguracionServiceImpl implements ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionServiceImpl(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Override
    public List<Configuracion> listarConfiguraciones() {
        return configuracionRepository.findAll();
    }

    @Override
    public Optional<Configuracion> obtenerConfiguracionPorId(Integer id) {
        return configuracionRepository.findById(id);
    }

    @Override
    public Optional<Configuracion> obtenerConfiguracionPorClave(String clave) {
        return configuracionRepository.findByClave(clave);
    }

    @Override
    public Configuracion guardarConfiguracion(Configuracion configuracion) {
        return configuracionRepository.save(configuracion);
    }

    @Override
    public void eliminarConfiguracion(Integer id) {
        configuracionRepository.deleteById(id);
    }

    @Override
    public List<Configuracion> buscarConfiguracionesPorClave(String clave) {
        return configuracionRepository.findByClaveContainingIgnoreCase(clave);
    }

    @Override
    public String obtenerValorConfiguracion(String clave, String valorPorDefecto) {
        return configuracionRepository.findByClave(clave)
                .map(Configuracion::getValor)
                .orElse(valorPorDefecto);
    }
}