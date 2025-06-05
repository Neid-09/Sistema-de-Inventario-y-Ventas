package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO para el resumen de inventario. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumenInventarioDTO {
    public Integer totalProductos;
    public Integer productosActivos;
    public Integer productosBajoStock;
    public Integer productosSobrestock;
    public BigDecimal valorTotalCosto;
    public BigDecimal valorTotalVenta;
    public Integer totalCategorias;

    public ResumenInventarioDTO() {}

    public ResumenInventarioDTO(Integer totalProductos, Integer productosActivos, 
                               Integer productosBajoStock, Integer productosSobrestock,
                               BigDecimal valorTotalCosto, BigDecimal valorTotalVenta, 
                               Integer totalCategorias) {
        this.totalProductos = totalProductos;
        this.productosActivos = productosActivos;
        this.productosBajoStock = productosBajoStock;
        this.productosSobrestock = productosSobrestock;
        this.valorTotalCosto = valorTotalCosto;
        this.valorTotalVenta = valorTotalVenta;
        this.totalCategorias = totalCategorias;
    }

    // Getters y Setters
    public Integer getTotalProductos() { return totalProductos; }
    public void setTotalProductos(Integer totalProductos) { this.totalProductos = totalProductos; }

    public Integer getProductosActivos() { return productosActivos; }
    public void setProductosActivos(Integer productosActivos) { this.productosActivos = productosActivos; }

    public Integer getProductosBajoStock() { return productosBajoStock; }
    public void setProductosBajoStock(Integer productosBajoStock) { this.productosBajoStock = productosBajoStock; }

    public Integer getProductosSobrestock() { return productosSobrestock; }
    public void setProductosSobrestock(Integer productosSobrestock) { this.productosSobrestock = productosSobrestock; }

    public BigDecimal getValorTotalCosto() { return valorTotalCosto; }
    public void setValorTotalCosto(BigDecimal valorTotalCosto) { this.valorTotalCosto = valorTotalCosto; }

    public BigDecimal getValorTotalVenta() { return valorTotalVenta; }
    public void setValorTotalVenta(BigDecimal valorTotalVenta) { this.valorTotalVenta = valorTotalVenta; }

    public Integer getTotalCategorias() { return totalCategorias; }
    public void setTotalCategorias(Integer totalCategorias) { this.totalCategorias = totalCategorias; }
}
