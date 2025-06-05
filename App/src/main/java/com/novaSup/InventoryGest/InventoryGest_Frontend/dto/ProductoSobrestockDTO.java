package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO para productos con sobrestock. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoSobrestockDTO {
    public Integer idProducto;
    public String codigo;
    public String nombre;
    public Integer stock;
    public Integer stockMaximo;
    public Integer diferencia;
    public BigDecimal precioCosto;
    public BigDecimal precioVenta;
    public BigDecimal valorExceso;

    public ProductoSobrestockDTO() {}

    public ProductoSobrestockDTO(Integer idProducto, String codigo, String nombre, 
                                Integer stock, Integer stockMaximo, Integer diferencia,
                                BigDecimal precioCosto, BigDecimal precioVenta, BigDecimal valorExceso) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.stockMaximo = stockMaximo;
        this.diferencia = diferencia;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.valorExceso = valorExceso;
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

    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }

    public Integer getDiferencia() { return diferencia; }
    public void setDiferencia(Integer diferencia) { this.diferencia = diferencia; }

    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal precioCosto) { this.precioCosto = precioCosto; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public BigDecimal getValorExceso() { return valorExceso; }
    public void setValorExceso(BigDecimal valorExceso) { this.valorExceso = valorExceso; }
}
