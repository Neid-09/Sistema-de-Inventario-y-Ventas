package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import java.math.BigDecimal;
import java.util.List;

public class VentaRequest {
    private Integer idCliente;
    private Integer idVendedor;
    private Boolean requiereFactura;
    private Boolean aplicarImpuestos;
    private String numeroVenta;
    private List<DetalleVenta> detalles;

    // Getters and Setters
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

    public Boolean getRequiereFactura() {
        return requiereFactura;
    }

    public void setRequiereFactura(Boolean requiereFactura) {
        this.requiereFactura = requiereFactura;
    }

    public Boolean getAplicarImpuestos() {
        return aplicarImpuestos;
    }

    public void setAplicarImpuestos(Boolean aplicarImpuestos) {
        this.aplicarImpuestos = aplicarImpuestos;
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public static class DetalleVenta {
        private Integer idProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        // Getters and Setters
        public Integer getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
    }
}