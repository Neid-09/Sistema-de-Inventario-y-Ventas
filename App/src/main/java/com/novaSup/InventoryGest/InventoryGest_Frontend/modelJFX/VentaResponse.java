package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class VentaResponse {
    private Integer idVenta;
    private Timestamp fecha;
    private Integer idCliente;
    private Integer idVendedor;
    private String nombreVendedor;
    private BigDecimal total;
    private Boolean requiereFactura;
    private String numeroVenta;
    private Boolean aplicarImpuestos;

    // Getters and Setters
    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Boolean getRequiereFactura() {
        return requiereFactura;
    }

    public void setRequiereFactura(Boolean requiereFactura) {
        this.requiereFactura = requiereFactura;
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public Boolean getAplicarImpuestos() {
        return aplicarImpuestos;
    }

    public void setAplicarImpuestos(Boolean aplicarImpuestos) {
        this.aplicarImpuestos = aplicarImpuestos;
    }
}