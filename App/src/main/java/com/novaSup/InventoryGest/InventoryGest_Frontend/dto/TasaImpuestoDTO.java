package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para TasaImpuesto. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TasaImpuestoDTO {
    private Integer idTasa;
    private TipoImpuestoDTO tipoImpuesto; // Referencia al DTO completo
    private Double tasa;
    private String fechaInicio; // Formato ISO YYYY-MM-DD
    private String fechaFin;    // Formato ISO YYYY-MM-DD, puede ser null
    private String descripcion;

    // Constructor por defecto
    public TasaImpuestoDTO() {
    }

    // Constructor con campos (opcional)
    public TasaImpuestoDTO(Integer idTasa, TipoImpuestoDTO tipoImpuesto, Double tasa, String fechaInicio, String fechaFin, String descripcion) {
        this.idTasa = idTasa;
        this.tipoImpuesto = tipoImpuesto;
        this.tasa = tasa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getIdTasa() {
        return idTasa;
    }

    public void setIdTasa(Integer idTasa) {
        this.idTasa = idTasa;
    }

    public TipoImpuestoDTO getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuestoDTO tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public Double getTasa() {
        return tasa;
    }

    public void setTasa(Double tasa) {
        this.tasa = tasa;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
} 