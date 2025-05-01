package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EntradaProductoFX {

    private Integer idEntrada;
    private Integer idProducto;
    private String nombreProducto;
    private Integer idProveedor;
    private String nombreProveedor;
    private Integer cantidad;
    private LocalDateTime fecha;
    private String tipoMovimiento; // String en lugar de enum
    private BigDecimal precioUnitario;
    private String motivo;

    // Constantes para los tipos de movimiento
    public static final String TIPO_ENTRADA = "ENTRADA";
    public static final String TIPO_SALIDA = "SALIDA";

    // Constructores
    public EntradaProductoFX() {
    }

    // Constructor básico
    public EntradaProductoFX(Integer idEntrada, Integer idProducto, String nombreProducto,
                             Integer cantidad, LocalDateTime fecha, String tipoMovimiento,
                             BigDecimal precioUnitario) {
        this.idEntrada = idEntrada;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.precioUnitario = precioUnitario;
    }

    // Constructor completo
    public EntradaProductoFX(Integer idEntrada, Integer idProducto, String nombreProducto,
                             Integer idProveedor, String nombreProveedor, Integer cantidad,
                             LocalDateTime fecha, String tipoMovimiento,
                             BigDecimal precioUnitario, String motivo) {
        this.idEntrada = idEntrada;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.precioUnitario = precioUnitario;
        this.motivo = motivo;
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

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
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

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        return "EntradaProductoFX{" +
                "idEntrada=" + idEntrada +
                ", producto=" + nombreProducto +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                '}';
    }
}