package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> obtenerTodosLosClientes();
    Optional<Cliente> obtenerClientePorId(Integer id);
    Optional<Cliente> obtenerClientePorDocumentoIdentidad(String documentoIdentidad);
    Optional<Cliente> obtenerClientePorNombre(String nombre);
    Optional<Cliente> obtenerClientePorIdentificacionFiscal(String identificacionFiscal);
    List<Cliente> obtenerClientesPorEstado(boolean activo);
    Optional<Cliente> obtenerClientePorCelular(String celular);
    Cliente guardarCliente(Cliente cliente);
    Cliente actualizarCliente(Integer id, Cliente clienteDetalles);
    void eliminarCliente(Integer id);
    Cliente anadirPuntosFidelidad(Integer idCliente, int puntos);
    Cliente actualizarTotalComprado(Integer idCliente, java.math.BigDecimal montoCompra);
}
