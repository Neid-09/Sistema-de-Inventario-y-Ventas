package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Configuracion;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionService {

    List<Configuracion> listarConfiguraciones();

    Optional<Configuracion> obtenerConfiguracionPorId(Integer id);

    Optional<Configuracion> obtenerConfiguracionPorClave(String clave);

    Configuracion guardarConfiguracion(Configuracion configuracion);

    void eliminarConfiguracion(Integer id);

    List<Configuracion> buscarConfiguracionesPorClave(String clave);

    String obtenerValorConfiguracion(String clave, String valorPorDefecto);
}