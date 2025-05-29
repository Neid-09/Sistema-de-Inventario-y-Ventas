package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para TipoImpuesto. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TipoImpuestoDTO {
    private Integer idTipoImpuesto;
    private String nombre;
    private String descripcion;
    private Boolean esPorcentual;
    private Boolean activo;

    // Constructor por defecto
    public TipoImpuestoDTO() {
    }

    // Constructor con campos (opcional)
    public TipoImpuestoDTO(Integer idTipoImpuesto, String nombre, String descripcion, Boolean esPorcentual, Boolean activo) {
        this.idTipoImpuesto = idTipoImpuesto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esPorcentual = esPorcentual;
        this.activo = activo;
    }

    // Getters y Setters
    public Integer getIdTipoImpuesto() {
        return idTipoImpuesto;
    }

    public void setIdTipoImpuesto(Integer idTipoImpuesto) {
        this.idTipoImpuesto = idTipoImpuesto;
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

    public Boolean getEsPorcentual() {
        return esPorcentual;
    }

    public void setEsPorcentual(Boolean esPorcentual) {
        this.esPorcentual = esPorcentual;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
} 