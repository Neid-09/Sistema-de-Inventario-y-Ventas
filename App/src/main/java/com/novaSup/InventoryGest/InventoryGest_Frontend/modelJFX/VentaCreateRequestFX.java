package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import java.util.List;

// No se usan propiedades JavaFX aquí, es un DTO simple para la solicitud.
public class VentaCreateRequestFX {
    private Integer idCliente; // Opcional
    private Integer idVendedor;
    private Boolean requiereFactura;
    private Boolean aplicarImpuestos;
    private String numeroVenta; // Opcional
    private String tipoPago;
    private List<DetalleVentaCreateRequestFX> detalles;

    public VentaCreateRequestFX() {
    }

    public VentaCreateRequestFX(Integer idCliente, Integer idVendedor, Boolean requiereFactura, 
                                Boolean aplicarImpuestos, String numeroVenta, String tipoPago, 
                                List<DetalleVentaCreateRequestFX> detalles) {
        this.idCliente = idCliente;
        this.idVendedor = idVendedor;
        this.requiereFactura = requiereFactura;
        this.aplicarImpuestos = aplicarImpuestos;
        this.numeroVenta = numeroVenta;
        this.tipoPago = tipoPago;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Integer getIdVendedor() { return idVendedor; }
    public void setIdVendedor(Integer idVendedor) { this.idVendedor = idVendedor; }

    public Boolean getRequiereFactura() { return requiereFactura; }
    public void setRequiereFactura(Boolean requiereFactura) { this.requiereFactura = requiereFactura; }

    public Boolean getAplicarImpuestos() { return aplicarImpuestos; }
    public void setAplicarImpuestos(Boolean aplicarImpuestos) { this.aplicarImpuestos = aplicarImpuestos; }

    public String getNumeroVenta() { return numeroVenta; }
    public void setNumeroVenta(String numeroVenta) { this.numeroVenta = numeroVenta; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public List<DetalleVentaCreateRequestFX> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaCreateRequestFX> detalles) { this.detalles = detalles; }
} 