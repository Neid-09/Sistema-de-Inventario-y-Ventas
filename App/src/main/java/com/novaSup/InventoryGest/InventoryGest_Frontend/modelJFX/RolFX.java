package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RolFX {
    private final IntegerProperty idRol;
    private final StringProperty nombre;
    private ObservableSet<PermisoFX> permisos;

    public RolFX() {
        this.idRol = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.permisos = FXCollections.observableSet(new HashSet<>());
    }

    public RolFX(Integer idRol, String nombre) {
        this.idRol = new SimpleIntegerProperty(idRol);
        this.nombre = new SimpleStringProperty(nombre);
        this.permisos = FXCollections.observableSet(new HashSet<>());
    }

    // Getters y setters
    public Integer getIdRol() {
        return idRol.get();
    }

    public IntegerProperty idRolProperty() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol.set(idRol);
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

    public Set<PermisoFX> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<PermisoFX> permisos) {
        this.permisos.clear();
        if (permisos != null) {
            this.permisos.addAll(permisos);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolFX otroRol = (RolFX) o;
        return Objects.equals(this.getIdRol(), otroRol.getIdRol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIdRol());
    }

    @Override
    public String toString() {
        return nombre.get();
    }
}