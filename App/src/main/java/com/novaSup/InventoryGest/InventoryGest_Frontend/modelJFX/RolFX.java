package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RolFX {
    private final IntegerProperty idRol;
    private final StringProperty rol;

    public RolFX() {
        this.idRol = new SimpleIntegerProperty();
        this.rol = new SimpleStringProperty();
    }

    public RolFX(Integer idRol, String rol) {
        this.idRol = new SimpleIntegerProperty(idRol);
        this.rol = new SimpleStringProperty(rol);
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

    @Override
    public String toString() {
        return rol.get(); // Para mostrar en ComboBox
    }
}