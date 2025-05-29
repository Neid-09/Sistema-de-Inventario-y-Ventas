package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import java.math.BigDecimal;

/**
 * Clase para transportar información esencial de un producto dentro del proceso de venta.
 * Utilizada para pasar datos entre VenderControllerFX y ProcesarVentaDialogController,
 * y potencialmente para la generación de tickets/facturas.
 */

 //PARA DETALLE DE VENTA
public class ProductoVentaInfo {
    private int idProducto;
    private String nombre;
    private int cantidad;
    private BigDecimal precioVentaUnitario;
    private BigDecimal subtotal; // Calculado como cantidad * precioVentaUnitario

    // Constructor
    public ProductoVentaInfo(int idProducto, String nombre, int cantidad, BigDecimal precioVentaUnitario) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioVentaUnitario = precioVentaUnitario;
        this.subtotal = precioVentaUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters
    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioVentaUnitario() {
        return precioVentaUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    // Setters (si fueran necesarios, aunque típicamente es inmutable o se recalcula)
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.precioVentaUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
