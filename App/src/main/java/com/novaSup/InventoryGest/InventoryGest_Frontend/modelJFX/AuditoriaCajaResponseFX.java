package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditoriaCajaResponseFX {
    private final IntegerProperty idAuditoria;
    private final ObjectProperty<CajaResponseFX> caja;
    private final ObjectProperty<BigDecimal> dineroEsperado;
    private final ObjectProperty<BigDecimal> dineroReal;
    private final ObjectProperty<LocalDateTime> fecha;
    private final StringProperty motivo;
    private final ObjectProperty<UsuarioSimplifiedFX> usuario;

    public AuditoriaCajaResponseFX() {
        this.idAuditoria = new SimpleIntegerProperty(this, "idAuditoria");
        this.caja = new SimpleObjectProperty<>(this, "caja");
        this.dineroEsperado = new SimpleObjectProperty<>(this, "dineroEsperado");
        this.dineroReal = new SimpleObjectProperty<>(this, "dineroReal");
        this.fecha = new SimpleObjectProperty<>(this, "fecha");
        this.motivo = new SimpleStringProperty(this, "motivo");
        this.usuario = new SimpleObjectProperty<>(this, "usuario");
    }

    // Getters para Properties
    public IntegerProperty idAuditoriaProperty() {
        return idAuditoria;
    }

    public ObjectProperty<CajaResponseFX> cajaProperty() {
        return caja;
    }

    public ObjectProperty<BigDecimal> dineroEsperadoProperty() {
        return dineroEsperado;
    }

    public ObjectProperty<BigDecimal> dineroRealProperty() {
        return dineroReal;
    }

    public ObjectProperty<LocalDateTime> fechaProperty() {
        return fecha;
    }

    public StringProperty motivoProperty() {
        return motivo;
    }

    public ObjectProperty<UsuarioSimplifiedFX> usuarioProperty() {
        return usuario;
    }

    // Getters
    public int getIdAuditoria() {
        return idAuditoria.get();
    }

    public CajaResponseFX getCaja() {
        return caja.get();
    }

    public BigDecimal getDineroEsperado() {
        return dineroEsperado.get();
    }

    public BigDecimal getDineroReal() {
        return dineroReal.get();
    }

    public LocalDateTime getFecha() {
        return fecha.get();
    }

    public String getMotivo() {
        return motivo.get();
    }

    public UsuarioSimplifiedFX getUsuario() {
        return usuario.get();
    }

    // Setters
    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria.set(idAuditoria);
    }

    public void setCaja(CajaResponseFX caja) {
        this.caja.set(caja);
    }

    public void setDineroEsperado(BigDecimal dineroEsperado) {
        this.dineroEsperado.set(dineroEsperado);
    }

    public void setDineroReal(BigDecimal dineroReal) {
        this.dineroReal.set(dineroReal);
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha.set(fecha);
    }

    public void setMotivo(String motivo) {
        this.motivo.set(motivo);
    }

    public void setUsuario(UsuarioSimplifiedFX usuario) {
        this.usuario.set(usuario);
    }
} 