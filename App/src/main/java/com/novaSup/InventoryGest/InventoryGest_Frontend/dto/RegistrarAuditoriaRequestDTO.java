package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import java.math.BigDecimal;

public class RegistrarAuditoriaRequestDTO {
    private Integer idCaja;
    private BigDecimal dineroEsperado;
    private BigDecimal dineroReal;
    private String motivo;
    private Integer idUsuario;

    // Getters y Setters
    public Integer getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }

    public BigDecimal getDineroEsperado() {
        return dineroEsperado;
    }

    public void setDineroEsperado(BigDecimal dineroEsperado) {
        this.dineroEsperado = dineroEsperado;
    }

    public BigDecimal getDineroReal() {
        return dineroReal;
    }

    public void setDineroReal(BigDecimal dineroReal) {
        this.dineroReal = dineroReal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
} 