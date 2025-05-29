package com.novaSup.InventoryGest.InventoryGest_Frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO para Producto. Usado para comunicación con la API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO {
    public Integer idProducto;
    public String codigo;
    public String nombre;
    public String descripcion;
    public BigDecimal precioCosto;
    public BigDecimal precioVenta;
    public Integer stock;
    public Integer stockMinimo;
    public Integer stockMaximo;
    public Integer idCategoria; // ID para enviar al backend
    public CategoriaDTO categoria; // Objeto completo recibido del backend
    public Integer idProveedor; // ID para enviar al backend
    public ProveedorDTO proveedor; // Objeto completo recibido del backend
    public Boolean estado;

    // Constructor por defecto
    public ProductoDTO() {
    }

    // Getters y Setters (importantes para Jackson si los campos no son public)
    // (Puedes generarlos o dejar los campos public si prefieres)

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal precioCosto) { this.precioCosto = precioCosto; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    public CategoriaDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaDTO categoria) { this.categoria = categoria; }
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }
    public ProveedorDTO getProveedor() { return proveedor; }
    public void setProveedor(ProveedorDTO proveedor) { this.proveedor = proveedor; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
} 