package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para Proveedor. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProveedorDTO {
    public Integer idProveedor;
    public String nombre;
    public String contacto;
    public String telefono;
    public String correo;
    public String direccion;

    // Constructor por defecto
    public ProveedorDTO() {
    }

    // Getters y Setters
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
} 