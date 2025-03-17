package com.novaSup.InventoryGest.InventoryGest_Backend.services;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Administrador;
import com.novaSup.InventoryGest.InventoryGest_Backend.repositories.AdministradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {
    private final AdministradorRepository administradorRepository;

    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public List<Administrador> obtenerTodos() {
        return administradorRepository.findAll();
    }

    public Optional<Administrador> obtenerPorId(int id) {
        return administradorRepository.findById(id);
    }

    public Administrador guardar(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    public void eliminar(int id) {
        administradorRepository.deleteById(id);
    }
}
