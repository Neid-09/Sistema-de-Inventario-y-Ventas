package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TipoImpuestoFX {

    private final ObjectProperty<Integer> idTipoImpuesto;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final BooleanProperty esPorcentual;
    private final BooleanProperty activo;

    public TipoImpuestoFX(Integer idTipoImpuesto, String nombre, String descripcion, Boolean esPorcentual, Boolean activo) {
        this.idTipoImpuesto = new SimpleObjectProperty<>(idTipoImpuesto);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.esPorcentual = new SimpleBooleanProperty(esPorcentual != null && esPorcentual);
        this.activo = new SimpleBooleanProperty(activo != null && activo);
    }

    public TipoImpuestoFX() {
        this.idTipoImpuesto = new SimpleObjectProperty<>();
        this.nombre = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
        this.esPorcentual = new SimpleBooleanProperty();
        this.activo = new SimpleBooleanProperty();
    }

    // idTipoImpuesto
    public Integer getIdTipoImpuesto() {
        return idTipoImpuesto.get();
    }
    public void setIdTipoImpuesto(Integer idTipoImpuesto) {
        this.idTipoImpuesto.set(idTipoImpuesto);
    }
    public ObjectProperty<Integer> idTipoImpuestoProperty() {
        return idTipoImpuesto;
    }

    // nombre
    public String getNombre() {
        return nombre.get();
    }
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }
    public StringProperty nombreProperty() {
        return nombre;
    }

    // descripcion
    public String getDescripcion() {
        return descripcion.get();
    }
    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    // esPorcentual
    public boolean isEsPorcentual() {
        return esPorcentual.get();
    }
    public void setEsPorcentual(boolean esPorcentual) {
        this.esPorcentual.set(esPorcentual);
    }
    public BooleanProperty esPorcentualProperty() {
        return esPorcentual;
    }

    // activo
    public boolean isActivo() {
        return activo.get();
    }
    public void setActivo(boolean activo) {
        this.activo.set(activo);
    }
    public BooleanProperty activoProperty() {
        return activo;
    }

    @Override
    public String toString() {
        return getNombre(); // Para mostrar en ComboBox u otros controles
    }
} 