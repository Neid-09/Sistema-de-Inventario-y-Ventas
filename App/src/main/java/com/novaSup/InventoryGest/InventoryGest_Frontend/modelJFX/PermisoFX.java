package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PermisoFX {
    private final IntegerProperty idPermiso;
    private final StringProperty nombre;
    private final StringProperty descripcion;

    public PermisoFX() {
        this.idPermiso = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
    }

    public PermisoFX(Integer idPermiso, String nombre, String descripcion) {
        this.idPermiso = new SimpleIntegerProperty(idPermiso);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    // Getters y setters
    public Integer getIdPermiso() {
        return idPermiso.get();
    }

    public IntegerProperty idPermisoProperty() {
        return idPermiso;
    }

    public void setIdPermiso(Integer idPermiso) {
        this.idPermiso.set(idPermiso);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermisoFX permisoFX = (PermisoFX) o;
        return idPermiso.get() == permisoFX.idPermiso.get();
    }

    @Override
    public int hashCode() {
        return idPermiso.get();
    }
}