package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;

public class RolFX {
    private final IntegerProperty idRol;
    private final StringProperty rol;
    private final ObservableSet<PermisoFX> permisos;

    public RolFX() {
        this.idRol = new SimpleIntegerProperty();
        this.rol = new SimpleStringProperty();
        this.permisos = FXCollections.observableSet();
    }

    public RolFX(Integer idRol, String rol) {
        this.idRol = new SimpleIntegerProperty(idRol);
        this.rol = new SimpleStringProperty(rol);
        this.permisos = FXCollections.observableSet();
    }

    public RolFX(Integer idRol, String rol, Set<PermisoFX> permisos) {
        this.idRol = new SimpleIntegerProperty(idRol);
        this.rol = new SimpleStringProperty(rol);
        this.permisos = FXCollections.observableSet(permisos);
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

    public String getRol() {
        return rol.get();
    }

    public StringProperty rolProperty() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol.set(rol);
    }

    public ObservableSet<PermisoFX> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<PermisoFX> permisos) {
        this.permisos.clear();
        if (permisos != null) {
            this.permisos.addAll(permisos);
        }
    }

    @Override
    public String toString() {
        return rol.get();
    }
}