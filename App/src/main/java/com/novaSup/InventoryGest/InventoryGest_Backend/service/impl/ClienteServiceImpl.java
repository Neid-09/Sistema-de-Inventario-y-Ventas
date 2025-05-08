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
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorNombre(String nombre) {
        return clienteRepository.findByNombre(nombre);
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
        // Establecer valor por defecto para limiteCredito si es nulo al crear
        if (cliente.getLimiteCredito() == null) {
            cliente.setLimiteCredito(new BigDecimal("1000.00")); // Asumiendo 1000.00 como default
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(Integer id, Cliente clienteDetalles) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));

        // Actualizar y validar cédula si es necesario
        String nuevaCedula = clienteDetalles.getCedula();
        // Solo validar si la cédula se proporciona y es diferente a la actual (o la actual es null)
        if (nuevaCedula != null && (cliente.getCedula() == null || !cliente.getCedula().equals(nuevaCedula))) {
            Optional<Cliente> cedulaExistente = clienteRepository.findByCedula(nuevaCedula)
                    .filter(c -> !c.getIdCliente().equals(id));
            if (cedulaExistente.isPresent()) {
                throw new IllegalArgumentException("La nueva cédula ya está registrada por otro cliente.");
            }
        }
        // Asignar la cédula proporcionada (puede ser null si se envió así explícitamente o si no se envió y el DTO la tiene null)
        cliente.setCedula(nuevaCedula);

        // Actualizar y validar correo si es necesario
        String nuevoCorreo = clienteDetalles.getCorreo();
        // Solo validar si el correo se proporciona y es diferente al actual (o el actual es null)
        if (nuevoCorreo != null && (cliente.getCorreo() == null || !cliente.getCorreo().equals(nuevoCorreo))) {
            Optional<Cliente> correoExistente = clienteRepository.findByCorreo(nuevoCorreo)
                    .filter(c -> !c.getIdCliente().equals(id));
            if (correoExistente.isPresent()) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está registrado por otro cliente.");
            }
        }
        // Asignar el correo proporcionado
        cliente.setCorreo(nuevoCorreo);

        // Campos de asignación directa (si se envían nulos y el campo no es nullable=false en BD, se harán null)
        cliente.setNombre(clienteDetalles.getNombre());
        cliente.setCelular(clienteDetalles.getCelular());
        cliente.setDireccion(clienteDetalles.getDireccion());

        // Actualizar otros campos solo si se proporcionan explícitamente (no nulos en la solicitud)
        if (clienteDetalles.getRequiereFacturaDefault() != null) {
            cliente.setRequiereFacturaDefault(clienteDetalles.getRequiereFacturaDefault());
        }
        if (clienteDetalles.getRazonSocialFiscal() != null) {
            cliente.setRazonSocialFiscal(clienteDetalles.getRazonSocialFiscal());
        }
        if (clienteDetalles.getRfcFiscal() != null) {
            cliente.setRfcFiscal(clienteDetalles.getRfcFiscal());
        }
        if (clienteDetalles.getDireccionFiscal() != null) {
            cliente.setDireccionFiscal(clienteDetalles.getDireccionFiscal());
        }
        if (clienteDetalles.getCorreoFiscal() != null) {
            cliente.setCorreoFiscal(clienteDetalles.getCorreoFiscal());
        }
        if (clienteDetalles.getUsoCfdiDefault() != null) {
            cliente.setUsoCfdiDefault(clienteDetalles.getUsoCfdiDefault());
        }
        
        if (clienteDetalles.getLimiteCredito() != null) {
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

        // Nombre constante para el cliente general
        final String NOMBRE_CLIENTE_GENERAL = "Venta General";

        // No asignar puntos si es el cliente general o si los puntos a añadir son cero o negativos
        if (NOMBRE_CLIENTE_GENERAL.equals(cliente.getNombre()) || puntos <= 0) {
            return cliente; // Retornar el cliente sin modificar puntos
        }

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
