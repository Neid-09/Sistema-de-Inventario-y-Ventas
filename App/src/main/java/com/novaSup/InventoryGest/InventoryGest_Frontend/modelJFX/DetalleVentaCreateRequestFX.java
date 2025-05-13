package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

// No se usan propiedades JavaFX aquí, es un DTO simple para la solicitud.
public class DetalleVentaCreateRequestFX {
    private Integer idProducto;
    private Integer cantidad;
    // "precioUnitario" es opcional en la solicitud según el markdown, el backend lo tomará del producto si es nulo.
    // No lo incluimos aquí para mantenerlo simple, asumiendo que el backend maneja esto.

    public DetalleVentaCreateRequestFX() {
    }

    public DetalleVentaCreateRequestFX(Integer idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

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
} 