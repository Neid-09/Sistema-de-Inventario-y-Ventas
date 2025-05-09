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
    public Optional<Cliente> obtenerClientePorDocumentoIdentidad(String documentoIdentidad) {
        return clienteRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorNombre(String nombre) {
        return clienteRepository.findByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorIdentificacionFiscal(String identificacionFiscal) {
        return clienteRepository.findByIdentificacionFiscal(identificacionFiscal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientesPorEstado(boolean activo) {
        return clienteRepository.findByActivo(activo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorCelular(String celular) {
        return clienteRepository.findByCelular(celular);
    }

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        // Validaciones básicas (puedes añadir más)
        if (clienteRepository.findByDocumentoIdentidad(cliente.getDocumentoIdentidad()).isPresent()) {
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
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));

        // Actualizar solo los campos que se proporcionan (no son nulos) en clienteDetalles

        // Nombre (nullable = false en la entidad, pero se puede querer no actualizarlo si no se manda)
        if (clienteDetalles.getNombre() != null && !clienteDetalles.getNombre().isEmpty()) {
            clienteExistente.setNombre(clienteDetalles.getNombre());
        }

        // Documento de identidad (con validación de unicidad si cambia)
        if (clienteDetalles.getDocumentoIdentidad() != null && 
            !clienteDetalles.getDocumentoIdentidad().equals(clienteExistente.getDocumentoIdentidad())) {
            Optional<Cliente> otroClienteConMismoDocumento = clienteRepository.findByDocumentoIdentidad(clienteDetalles.getDocumentoIdentidad())
                    .filter(c -> !c.getIdCliente().equals(id));
            if (otroClienteConMismoDocumento.isPresent()) {
                throw new IllegalArgumentException("El nuevo documento de identidad ya está registrado por otro cliente.");
            }
            clienteExistente.setDocumentoIdentidad(clienteDetalles.getDocumentoIdentidad());
        } else if (clienteDetalles.getDocumentoIdentidad() != null && clienteDetalles.getDocumentoIdentidad().isEmpty()) {
            // Si se envía un documento vacío, podría interpretarse como querer borrarlo (si la lógica de negocio lo permite)
            // Por ahora, lo dejaremos tal cual o se puede lanzar un error si no se permite vacío.
            // Si la columna en BD no permite null, esto daría error al guardar.
            // Si se quiere permitir borrarlo, se asignaría: clienteExistente.setDocumentoIdentidad(null);
            // O clienteExistente.setDocumentoIdentidad(""); // si la BD lo permite
        }


        // Correo (con validación de unicidad si cambia)
        if (clienteDetalles.getCorreo() != null && 
            !clienteDetalles.getCorreo().equals(clienteExistente.getCorreo())) {
            Optional<Cliente> otroClienteConMismoCorreo = clienteRepository.findByCorreo(clienteDetalles.getCorreo())
                    .filter(c -> !c.getIdCliente().equals(id));
            if (otroClienteConMismoCorreo.isPresent()) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está registrado por otro cliente.");
            }
            clienteExistente.setCorreo(clienteDetalles.getCorreo());
        } else if (clienteDetalles.getCorreo() != null && clienteDetalles.getCorreo().isEmpty()) {
             // Permitir borrar el correo si se envía vacío y la entidad lo permite
             clienteExistente.setCorreo(null);
        }


        if (clienteDetalles.getCelular() != null) {
            clienteExistente.setCelular(clienteDetalles.getCelular().isEmpty() ? null : clienteDetalles.getCelular());
        }

        if (clienteDetalles.getDireccion() != null) {
            clienteExistente.setDireccion(clienteDetalles.getDireccion().isEmpty() ? null : clienteDetalles.getDireccion());
        }

        if (clienteDetalles.getRequiereFacturaDefault() != null) {
            clienteExistente.setRequiereFacturaDefault(clienteDetalles.getRequiereFacturaDefault());
        }

        if (clienteDetalles.getRazonSocial() != null) {
            clienteExistente.setRazonSocial(clienteDetalles.getRazonSocial().isEmpty() ? null : clienteDetalles.getRazonSocial());
        }

        if (clienteDetalles.getIdentificacionFiscal() != null) {
            clienteExistente.setIdentificacionFiscal(clienteDetalles.getIdentificacionFiscal().isEmpty() ? null : clienteDetalles.getIdentificacionFiscal());
        }

        if (clienteDetalles.getDireccionFacturacion() != null) {
            clienteExistente.setDireccionFacturacion(clienteDetalles.getDireccionFacturacion().isEmpty() ? null : clienteDetalles.getDireccionFacturacion());
        }

        if (clienteDetalles.getCorreoFacturacion() != null) {
            clienteExistente.setCorreoFacturacion(clienteDetalles.getCorreoFacturacion().isEmpty() ? null : clienteDetalles.getCorreoFacturacion());
        }

        if (clienteDetalles.getTipoFacturaDefault() != null) {
            clienteExistente.setTipoFacturaDefault(clienteDetalles.getTipoFacturaDefault().isEmpty() ? null : clienteDetalles.getTipoFacturaDefault());
        }
        
        if (clienteDetalles.getLimiteCredito() != null) {
             clienteExistente.setLimiteCredito(clienteDetalles.getLimiteCredito());
        }

        // Campos como 'totalComprado', 'puntosFidelidad', 'ultimaCompra'
        // generalmente no se actualizan directamente a través de este endpoint,
        // sino como resultado de otras operaciones (ej. una venta).
        // 'activo' y 'fechaRegistro' tampoco deberían actualizarse por esta vía.
        // 'fechaActualizacion' se actualiza automáticamente por @UpdateTimestamp

        if (clienteDetalles.getActivo() != null) {
            clienteExistente.setActivo(clienteDetalles.getActivo());
        }

        return clienteRepository.save(clienteExistente);
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
