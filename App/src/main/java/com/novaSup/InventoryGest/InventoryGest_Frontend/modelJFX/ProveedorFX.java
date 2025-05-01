package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

public class ProveedorFX {
    private final IntegerProperty idProveedor;
    private final StringProperty nombre;
    private final StringProperty contacto;
    private final StringProperty telefono;
    private final StringProperty correo;
    private final StringProperty direccion;

    public ProveedorFX(Integer idProveedor, String nombre, String contacto,
                       String telefono, String correo, String direccion) {
        this.idProveedor = new SimpleIntegerProperty(idProveedor != null ? idProveedor : 0);
        this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
        this.contacto = new SimpleStringProperty(contacto != null ? contacto : "");
        this.telefono = new SimpleStringProperty(telefono != null ? telefono : "");
        this.correo = new SimpleStringProperty(correo != null ? correo : "");
        this.direccion = new SimpleStringProperty(direccion != null ? direccion : "");
    }

    // Propiedades JavaFX
    public IntegerProperty idProveedorProperty() { return idProveedor; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty contactoProperty() { return contacto; }
    public StringProperty telefonoProperty() { return telefono; }
    public StringProperty correoProperty() { return correo; }
    public StringProperty direccionProperty() { return direccion; }

    // Getters
    public Integer getIdProveedor() { return idProveedor.get(); }
    public String getNombre() { return nombre.get(); }
    public String getContacto() { return contacto.get(); }
    public String getTelefono() { return telefono.get(); }
    public String getCorreo() { return correo.get(); }
    public String getDireccion() { return direccion.get(); }

    @Override
    public String toString() {
        return nombre.get();
    }
}