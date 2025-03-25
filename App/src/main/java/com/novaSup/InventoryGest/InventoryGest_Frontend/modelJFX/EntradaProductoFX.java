package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EntradaProductoFX {

    private Integer idEntrada;
    private Integer idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal precioUnitario;

    // Enumeración para el tipo de movimiento
    public enum TipoMovimiento {
        ENTRADA("Entrada"),
        SALIDA("Salida");

        private final String descripcion;

        TipoMovimiento(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    // Constructores
    public EntradaProductoFX() {
    }

    // Actualizar constructor
    public EntradaProductoFX(Integer idEntrada, Integer idProducto, String nombreProducto,
                             Integer cantidad, LocalDateTime fecha, TipoMovimiento tipoMovimiento,
                             BigDecimal precioUnitario) {
        this.idEntrada = idEntrada;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.precioUnitario = precioUnitario;
    }

    // Getters y setters
    public Integer getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(Integer idEntrada) {
        this.idEntrada = idEntrada;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    // Agregar getter y setter
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

}