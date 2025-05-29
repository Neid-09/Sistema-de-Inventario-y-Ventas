package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoCajaResponseFX {
    private final IntegerProperty idMovimiento;
    private final ObjectProperty<CajaResponseFX> caja;
    private final StringProperty tipoMovimiento;
    private final StringProperty descripcion;
    private final ObjectProperty<BigDecimal> monto;
    private final ObjectProperty<LocalDateTime> fecha;
    private final ObjectProperty<UsuarioSimplifiedFX> usuario;

    public MovimientoCajaResponseFX() {
        this.idMovimiento = new SimpleIntegerProperty(this, "idMovimiento");
        this.caja = new SimpleObjectProperty<>(this, "caja");
        this.tipoMovimiento = new SimpleStringProperty(this, "tipoMovimiento");
        this.descripcion = new SimpleStringProperty(this, "descripcion");
        this.monto = new SimpleObjectProperty<>(this, "monto");
        this.fecha = new SimpleObjectProperty<>(this, "fecha");
        this.usuario = new SimpleObjectProperty<>(this, "usuario");
    }

    // Getters para Properties
    public IntegerProperty idMovimientoProperty() {
        return idMovimiento;
    }

    public ObjectProperty<CajaResponseFX> cajaProperty() {
        return caja;
    }

    public StringProperty tipoMovimientoProperty() {
        return tipoMovimiento;
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public ObjectProperty<BigDecimal> montoProperty() {
        return monto;
    }

    public ObjectProperty<LocalDateTime> fechaProperty() {
        return fecha;
    }

    public ObjectProperty<UsuarioSimplifiedFX> usuarioProperty() {
        return usuario;
    }

    // Getters
    public int getIdMovimiento() {
        return idMovimiento.get();
    }

    public CajaResponseFX getCaja() {
        return caja.get();
    }

    public String getTipoMovimiento() {
        return tipoMovimiento.get();
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public BigDecimal getMonto() {
        return monto.get();
    }

    public LocalDateTime getFecha() {
        return fecha.get();
    }

    public UsuarioSimplifiedFX getUsuario() {
        return usuario.get();
    }

    // Setters
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento.set(idMovimiento);
    }

    public void setCaja(CajaResponseFX caja) {
        this.caja.set(caja);
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento.set(tipoMovimiento);
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public void setMonto(BigDecimal monto) {
        this.monto.set(monto);
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha.set(fecha);
    }

    public void setUsuario(UsuarioSimplifiedFX usuario) {
        this.usuario.set(usuario);
    }
} 