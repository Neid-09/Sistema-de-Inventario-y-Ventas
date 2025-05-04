// ProveedorServiceImpl.java
package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProveedorRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProveedorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    @Override
    public Optional<Proveedor> obtenerPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public void eliminar(Integer id) {
        proveedorRepository.deleteById(id);
    }

    // ProveedorServiceImpl.java
    @Override
    public List<Proveedor> buscarPorNombreOCorreo(String termino) {
        return proveedorRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCase(termino, termino);
    }
}