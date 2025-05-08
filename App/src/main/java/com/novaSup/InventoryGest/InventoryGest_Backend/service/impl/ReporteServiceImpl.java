package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaReporteDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ClienteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ReporteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VentaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final VentaService ventaService;
    private final ClienteService clienteService;

    public ReporteServiceImpl(VentaService ventaService, ClienteService clienteService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
    }

    @Override
    public List<VentaReporteDTO> generarReporteVentas() {
        List<Venta> ventas = ventaService.listarVentas();
        return ventas.stream().map(this::mapToVentaReporteDTO).collect(Collectors.toList());
    }

    private VentaReporteDTO mapToVentaReporteDTO(Venta venta) {
        VentaReporteDTO dto = new VentaReporteDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setNumeroVenta(venta.getNumeroVenta());
        dto.setFecha(venta.getFecha());

        Vendedor vendedor = venta.getVendedor();
        if (vendedor != null) {
            Usuario usuarioAsociado = vendedor.getUsuario();
            if (usuarioAsociado != null) {
                dto.setNombreVendedor(usuarioAsociado.getNombre());
            } else {
                dto.setNombreVendedor("Usuario no asignado al vendedor"); 
            }
        } else {
            dto.setNombreVendedor("Vendedor no asignado a la venta");
        }

        dto.setFormaDePago(venta.getTipoPago()); 

        if (Boolean.TRUE.equals(venta.getRequiereFactura())) {
            if (venta.getIdCliente() != null) {
                Optional<Cliente> clienteOpt = clienteService.obtenerClientePorId(venta.getIdCliente());
                if (clienteOpt.isPresent()) {
                    Cliente cliente = clienteOpt.get();
                    dto.setNombreCliente(cliente.getNombre());
                } else {
                    dto.setNombreCliente("Cliente Factura ID: " + venta.getIdCliente() + " (No encontrado)");
                }
            } else {
                dto.setNombreCliente("Factura Solicitada (Cliente no especificado)");
            }
        } else {
            dto.setNombreCliente("Público General");
        }

        dto.setTotalVenta(venta.getTotal());

        return dto;
    }
} 