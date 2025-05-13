package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para Categoria. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos extra en el JSON que no estén aquí
public class CategoriaDTO {
    public Integer idCategoria;
    public String nombre;
    public String descripcion;
    public Boolean estado;
    public Integer duracionGarantia;

    // Constructor por defecto necesario para Jackson
    public CategoriaDTO() {
    }

    // Constructor opcional con campos
    public CategoriaDTO(Integer idCategoria, String nombre, String descripcion, Boolean estado, Integer duracionGarantia) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.duracionGarantia = duracionGarantia;
    }

    // Getters y Setters (opcional si los campos son públicos, pero buena práctica añadirlos)

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getDuracionGarantia() {
        return duracionGarantia;
    }

    public void setDuracionGarantia(Integer duracionGarantia) {
        this.duracionGarantia = duracionGarantia;
    }
} 