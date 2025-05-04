package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ClienteRepository; // Asegúrate que el nombre del repositorio coincida
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    // Nota: Asegúrate que el nombre aquí coincida con tu clase repositorio.
    // Si renombraste ClienteRespository a ClienteRepository, usa ClienteRepository aquí.
    // Si no, usa ClienteRespository. Asumiré ClienteRepository por convención.
    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

     @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorCedula(String cedula) {
        return clienteRepository.findByCedula(cedula);
    }

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        // Validaciones básicas (puedes añadir más)
        if (clienteRepository.findByCedula(cliente.getCedula()).isPresent()) {
            throw new IllegalArgumentException("La cédula ya está registrada.");
        }
        if (cliente.getCorreo() != null && clienteRepository.findByCorreo(cliente.getCorreo()).isPresent()) {
             throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        // Establecer valores por defecto si son nulos al crear (aunque ya están inicializados en el modelo)
        if (cliente.getTotalComprado() == null) {
            cliente.setTotalComprado(BigDecimal.ZERO);
        }
        if (cliente.getPuntosFidelidad() == null) {
            cliente.setPuntosFidelidad(0);
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(Integer id, Cliente clienteDetalles) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));

        // Validar unicidad de cédula y correo si cambian
        if (!cliente.getCedula().equals(clienteDetalles.getCedula()) &&
            clienteRepository.findByCedula(clienteDetalles.getCedula()).filter(c -> !c.getIdCliente().equals(id)).isPresent()) {
             throw new IllegalArgumentException("La nueva cédula ya está registrada por otro cliente.");
        }
         if (clienteDetalles.getCorreo() != null && !clienteDetalles.getCorreo().equals(cliente.getCorreo()) &&
            clienteRepository.findByCorreo(clienteDetalles.getCorreo()).filter(c -> !c.getIdCliente().equals(id)).isPresent()) {
             throw new IllegalArgumentException("El nuevo correo electrónico ya está registrado por otro cliente.");
        }


        cliente.setNombre(clienteDetalles.getNombre());
        cliente.setCedula(clienteDetalles.getCedula());
        cliente.setCelular(clienteDetalles.getCelular());
        cliente.setCorreo(clienteDetalles.getCorreo());
        cliente.setDireccion(clienteDetalles.getDireccion());
        // Actualizar el nuevo campo
        if (clienteDetalles.getLimiteCredito() != null) { // Solo actualizar si se proporciona un valor
             cliente.setLimiteCredito(clienteDetalles.getLimiteCredito());
        }
        // No actualizamos total_comprado, puntos_fidelidad ni ultima_compra directamente aquí

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminarCliente(Integer id) {
         if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente no encontrado con id: " + id);
        }
        // Considerar lógica adicional: ¿qué pasa con las ventas o recompensas asociadas?
        // Podrías necesitar eliminar o desasociar entidades relacionadas antes.
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Cliente anadirPuntosFidelidad(Integer idCliente, int puntos) {
         Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + idCliente));
        cliente.setPuntosFidelidad(cliente.getPuntosFidelidad() + puntos);
        return clienteRepository.save(cliente);
    }

     @Override
    @Transactional
    public Cliente actualizarTotalComprado(Integer idCliente, BigDecimal montoCompra) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + idCliente));
        cliente.setTotalComprado(cliente.getTotalComprado().add(montoCompra));
        cliente.setUltimaCompra(Timestamp.from(Instant.now())); // Actualizar fecha de última compra
        return clienteRepository.save(cliente);
    }
}
