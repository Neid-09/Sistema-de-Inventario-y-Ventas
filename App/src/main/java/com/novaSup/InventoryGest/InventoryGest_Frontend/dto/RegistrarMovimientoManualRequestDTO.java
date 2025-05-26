package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import java.math.BigDecimal;

public class RegistrarMovimientoManualRequestDTO {
    private Integer idCaja;
    private String tipoMovimiento; // "ENTRADA" o "SALIDA"
    private String descripcion;
    private BigDecimal monto;
    private Integer idUsuario;

    // Getters y Setters
    public Integer getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
} 