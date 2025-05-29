package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO (Data Transfer Object) para representar la información de un ImpuestoAplicable
 * al comunicarse con el backend. Utiliza tipos Java estándar.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpuestoAplicableDTO {
    private Integer idImpuestoAplicable;
    private TasaImpuestoDTO tasaImpuesto;     // Usar TasaImpuestoDTO completo
    private ProductoDTO producto;             // Usar ProductoDTO completo
    private CategoriaDTO categoria;           // Usar CategoriaDTO completo
    private Boolean aplica;
    private String fechaInicio;               // Formato ISO YYYY-MM-DD como String
    private String fechaFin;                  // Formato ISO YYYY-MM-DD como String, puede ser null

    // Constructor por defecto (necesario para Jackson)
    public ImpuestoAplicableDTO() {
    }

    // Constructor completo (opcional, puede ser útil)
    public ImpuestoAplicableDTO(Integer idImpuestoAplicable, TasaImpuestoDTO tasaImpuesto, ProductoDTO producto, CategoriaDTO categoria, Boolean aplica, String fechaInicio, String fechaFin) {
        this.idImpuestoAplicable = idImpuestoAplicable;
        this.tasaImpuesto = tasaImpuesto;
        this.producto = producto;
        this.categoria = categoria;
        this.aplica = aplica;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters

    public Integer getIdImpuestoAplicable() {
        return idImpuestoAplicable;
    }

    public void setIdImpuestoAplicable(Integer idImpuestoAplicable) {
        this.idImpuestoAplicable = idImpuestoAplicable;
    }

    public TasaImpuestoDTO getTasaImpuesto() { // Cambiado a TasaImpuestoDTO
        return tasaImpuesto;
    }

    public void setTasaImpuesto(TasaImpuestoDTO tasaImpuesto) { // Cambiado a TasaImpuestoDTO
        this.tasaImpuesto = tasaImpuesto;
    }

    public ProductoDTO getProducto() { 
        return producto;
    }

    public void setProducto(ProductoDTO producto) { 
        this.producto = producto;
    }

    public CategoriaDTO getCategoria() { 
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) { 
        this.categoria = categoria;
    }

    public Boolean getAplica() {
        return aplica;
    }

    public void setAplica(Boolean aplica) {
        this.aplica = aplica;
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

    // --- Clases anidadas eliminadas --- 

} 