package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import java.math.BigDecimal;

public class AbrirCajaRequestDTO {
    private Integer idUsuario;
    private BigDecimal dineroInicial;
    private Boolean heredarSaldoAnterior;
    private String justificacionManual;

    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getDineroInicial() {
        return dineroInicial;
    }

    public void setDineroInicial(BigDecimal dineroInicial) {
        this.dineroInicial = dineroInicial;
    }

    public Boolean getHeredarSaldoAnterior() {
        return heredarSaldoAnterior;
    }

    public void setHeredarSaldoAnterior(Boolean heredarSaldoAnterior) {
        this.heredarSaldoAnterior = heredarSaldoAnterior;
    }

    public String getJustificacionManual() {
        return justificacionManual;
    }

    public void setJustificacionManual(String justificacionManual) {
        this.justificacionManual = justificacionManual;
    }
} 