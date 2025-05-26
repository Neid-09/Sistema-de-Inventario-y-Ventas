package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CajaResponseFX {
    private final IntegerProperty idCaja;
    private final ObjectProperty<UsuarioSimplifiedFX> usuario;
    private final ObjectProperty<LocalDateTime> fechaApertura;
    private final ObjectProperty<LocalDateTime> fechaCierre;
    private final ObjectProperty<BigDecimal> dineroInicial;
    private final ObjectProperty<BigDecimal> dineroTotal;
    private final StringProperty estado;

    public CajaResponseFX() {
        this.idCaja = new SimpleIntegerProperty(this, "idCaja");
        this.usuario = new SimpleObjectProperty<>(this, "usuario");
        this.fechaApertura = new SimpleObjectProperty<>(this, "fechaApertura");
        this.fechaCierre = new SimpleObjectProperty<>(this, "fechaCierre");
        this.dineroInicial = new SimpleObjectProperty<>(this, "dineroInicial");
        this.dineroTotal = new SimpleObjectProperty<>(this, "dineroTotal");
        this.estado = new SimpleStringProperty(this, "estado");
    }

    // Getters para Properties
    public IntegerProperty idCajaProperty() {
        return idCaja;
    }

    public ObjectProperty<UsuarioSimplifiedFX> usuarioProperty() {
        return usuario;
    }

    public ObjectProperty<LocalDateTime> fechaAperturaProperty() {
        return fechaApertura;
    }

    public ObjectProperty<LocalDateTime> fechaCierreProperty() {
        return fechaCierre;
    }

    public ObjectProperty<BigDecimal> dineroInicialProperty() {
        return dineroInicial;
    }

    public ObjectProperty<BigDecimal> dineroTotalProperty() {
        return dineroTotal;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    // Getters
    public int getIdCaja() {
        return idCaja.get();
    }

    public UsuarioSimplifiedFX getUsuario() {
        return usuario.get();
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura.get();
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre.get();
    }

    public BigDecimal getDineroInicial() {
        return dineroInicial.get();
    }

    public BigDecimal getDineroTotal() {
        return dineroTotal.get();
    }

    public String getEstado() {
        return estado.get();
    }

    // Setters
    public void setIdCaja(int idCaja) {
        this.idCaja.set(idCaja);
    }

    public void setUsuario(UsuarioSimplifiedFX usuario) {
        this.usuario.set(usuario);
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura.set(fechaApertura);
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre.set(fechaCierre);
    }

    public void setDineroInicial(BigDecimal dineroInicial) {
        this.dineroInicial.set(dineroInicial);
    }

    public void setDineroTotal(BigDecimal dineroTotal) {
        this.dineroTotal.set(dineroTotal);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }
} 