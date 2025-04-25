package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class ProductoFX {
    private final IntegerProperty idProducto;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final ObjectProperty<BigDecimal> precio;
    private final IntegerProperty stock;
    private final BooleanProperty estado;

    public ProductoFX(Integer idProducto, String nombre, String descripcion, BigDecimal precio, Integer stock, Boolean estado) {
        this.idProducto = new SimpleIntegerProperty(idProducto != null ? idProducto : 0);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.precio = new SimpleObjectProperty<>(precio);
        this.stock = new SimpleIntegerProperty(stock != null ? stock : 0);
        this.estado = new SimpleBooleanProperty(estado != null ? estado : true);
    }

    // Getters para propiedades
    public IntegerProperty idProductoProperty() { return idProducto; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty descripcionProperty() { return descripcion; }
    public ObjectProperty<BigDecimal> precioProperty() { return precio; }
    public IntegerProperty stockProperty() { return stock; }
    public BooleanProperty estadoProperty() { return estado; }

    // Getters para valores
    public Integer getIdProducto() { return idProducto.get(); }
    public String getNombre() { return nombre.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public BigDecimal getPrecio() { return precio.get(); }
    public Integer getStock() { return stock.get(); }
    public Boolean getEstado() { return estado.get(); }

    // Setter para stock
    public void setStock(Integer stock) {
        this.stock.set(stock);
    }
}