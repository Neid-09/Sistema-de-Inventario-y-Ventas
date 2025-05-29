package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.BooleanProperty; // Importar BooleanProperty
import javafx.beans.property.SimpleBooleanProperty; // Importar SimpleBooleanProperty

public class UsuarioFX {
    private final IntegerProperty idUsuario;
    private final StringProperty nombre;
    private final StringProperty correo;
    private final StringProperty telefono;
    private final StringProperty contraseña;
    private final ObjectProperty<RolFX> rol;
    private final BooleanProperty estado; // Nueva propiedad estado

    public UsuarioFX() {
        this.idUsuario = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.correo = new SimpleStringProperty();
        this.telefono = new SimpleStringProperty();
        this.contraseña = new SimpleStringProperty();
        this.rol = new SimpleObjectProperty<>();
        this.estado = new SimpleBooleanProperty(); // Inicializar estado
    }

    // Constructor actualizado para incluir estado
    public UsuarioFX(Integer idUsuario, String nombre, String correo, String telefono, String contraseña, RolFX rol, Boolean estado) {
        // Si idUsuario es null, inicializa la propiedad sin valor
        if (idUsuario == null) {
            this.idUsuario = new SimpleIntegerProperty();
        } else {
            this.idUsuario = new SimpleIntegerProperty(idUsuario);
        }

        this.nombre = new SimpleStringProperty(nombre);
        this.correo = new SimpleStringProperty(correo);
        this.telefono = new SimpleStringProperty(telefono);
        this.contraseña = new SimpleStringProperty(contraseña);
        this.rol = new SimpleObjectProperty<>(rol);
        this.estado = new SimpleBooleanProperty(estado != null ? estado : false); // Inicializar estado, default a false si es null
    }

    // Getters y setters
    public Integer getIdUsuario() {
        return idUsuario.get();
    }

    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario.set(idUsuario);
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

    public String getCorreo() {
        return correo.get();
    }

    public StringProperty correoProperty() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public String getTelefono() {
        return telefono.get();
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono.set(telefono);
    }

    public String getContraseña() {
        return contraseña.get();
    }

    public StringProperty contraseñaProperty() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña.set(contraseña);
    }

    public RolFX getRol() {
        return rol.get();
    }

    public ObjectProperty<RolFX> rolProperty() {
        return rol;
    }

    public void setRol(RolFX rol) {
        this.rol.set(rol);
    }

    // Getters y setters para estado
    public boolean isEstado() {
        return estado.get();
    }

    public BooleanProperty estadoProperty() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado.set(estado);
    }

}