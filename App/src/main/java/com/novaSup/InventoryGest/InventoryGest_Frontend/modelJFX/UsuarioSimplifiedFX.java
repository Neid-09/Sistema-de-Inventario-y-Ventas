package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsuarioSimplifiedFX {
    private final IntegerProperty idUsuario;
    private final StringProperty nombre;

    public UsuarioSimplifiedFX() {
        this.idUsuario = new SimpleIntegerProperty(this, "idUsuario");
        this.nombre = new SimpleStringProperty(this, "nombre");
    }

    public UsuarioSimplifiedFX(Integer idUsuario, String nombre) {
        this();
        setIdUsuario(idUsuario);
        setNombre(nombre);
    }

    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario.get();
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario.set(idUsuario);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }
} 