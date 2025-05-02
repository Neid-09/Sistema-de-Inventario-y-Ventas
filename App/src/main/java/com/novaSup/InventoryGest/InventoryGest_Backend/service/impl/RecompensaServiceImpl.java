package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Recompensa;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ClienteRepository; // Asegúrate que el nombre coincida
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RecompensaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.RecompensaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RecompensaServiceImpl implements RecompensaService {

    @Autowired
    private RecompensaRepository recompensaRepository;

    // Nota: Asegúrate que el nombre aquí coincida con tu clase repositorio de Cliente.
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Recompensa> obtenerRecompensasPorCliente(Integer idCliente) {
        // Verificar si el cliente existe primero podría ser una buena práctica
        if (!clienteRepository.existsById(idCliente)) {
             throw new EntityNotFoundException("Cliente no encontrado con id: " + idCliente);
        }
        return recompensaRepository.findByIdCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recompensa> obtenerRecompensaPorId(Integer idRecompensa) {
        return recompensaRepository.findById(idRecompensa);
    }

    @Override
    @Transactional
    public Recompensa canjearRecompensa(Integer idCliente, Recompensa recompensa) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + idCliente));

        if (recompensa.getPuntosUsados() == null || recompensa.getPuntosUsados() <= 0) {
            throw new IllegalArgumentException("Los puntos usados deben ser un valor positivo.");
        }
        if (cliente.getPuntosFidelidad() < recompensa.getPuntosUsados()) {
            throw new IllegalArgumentException("Puntos de fidelidad insuficientes.");
        }
        if (recompensa.getDescripcionRecompensa() == null || recompensa.getDescripcionRecompensa().trim().isEmpty()) {
             throw new IllegalArgumentException("La descripción de la recompensa no puede estar vacía.");
        }


        // Restar puntos al cliente
        cliente.setPuntosFidelidad(cliente.getPuntosFidelidad() - recompensa.getPuntosUsados());
        clienteRepository.save(cliente); // Guardar el estado actualizado del cliente

        // Preparar y guardar la recompensa
        recompensa.setIdCliente(idCliente); // Asegurar que el ID del cliente está establecido
        recompensa.setFechaReclamo(Timestamp.from(Instant.now())); // Establecer la fecha actual
        // El cliente asociado (@ManyToOne) se manejará automáticamente si la relación está bien configurada
        // y si pasas el objeto Cliente completo, pero aquí usamos idCliente directamente.

        return recompensaRepository.save(recompensa);
    }
}
