package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para productos con stock crítico. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoStockCriticoDTO {
    public Integer idProducto;
    public String codigo;
    public String nombre;
    public Integer stock;
    public Integer stockMinimo;
    public Integer diferencia;

    public ProductoStockCriticoDTO() {}

    public ProductoStockCriticoDTO(Integer idProducto, String codigo, String nombre, 
                                  Integer stock, Integer stockMinimo, Integer diferencia) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.diferencia = diferencia;
    }

    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Integer getDiferencia() { return diferencia; }
    public void setDiferencia(Integer diferencia) { this.diferencia = diferencia; }
}
