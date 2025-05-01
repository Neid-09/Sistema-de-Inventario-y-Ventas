package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LoteFX {
    private final IntegerProperty idLote;
    private final IntegerProperty idEntrada;
    private final ObjectProperty<ProductoFX> producto;
    private final IntegerProperty idProducto;
    private final StringProperty numeroLote;
    private final ObjectProperty<LocalDate> fechaEntrada;
    private final ObjectProperty<LocalDate> fechaVencimiento;
    private final IntegerProperty cantidad;
    private final BooleanProperty activo;

    public LoteFX() {
        this(null, null, null, null, null, null, null, null, true);
    }

    public LoteFX(Integer idLote, Integer idEntrada, ProductoFX producto, Integer idProducto,
                  String numeroLote, Date fechaEntrada, Date fechaVencimiento,
                  Integer cantidad, Boolean activo) {
        this.idLote = new SimpleIntegerProperty(idLote != null ? idLote : 0);
        this.idEntrada = new SimpleIntegerProperty(idEntrada != null ? idEntrada : 0);
        this.producto = new SimpleObjectProperty<>(producto);
        this.idProducto = new SimpleIntegerProperty(idProducto != null ? idProducto : 0);
        this.numeroLote = new SimpleStringProperty(numeroLote != null ? numeroLote : "");
        this.fechaEntrada = new SimpleObjectProperty<>(
                fechaEntrada != null ?
                        fechaEntrada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null);
        this.fechaVencimiento = new SimpleObjectProperty<>(
                fechaVencimiento != null ?
                        fechaVencimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null);
        this.cantidad = new SimpleIntegerProperty(cantidad != null ? cantidad : 0);
        this.activo = new SimpleBooleanProperty(activo != null ? activo : true);
    }

    // Propiedades JavaFX
    public IntegerProperty idLoteProperty() { return idLote; }
    public IntegerProperty idEntradaProperty() { return idEntrada; }
    public ObjectProperty<ProductoFX> productoProperty() { return producto; }
    public IntegerProperty idProductoProperty() { return idProducto; }
    public StringProperty numeroLoteProperty() { return numeroLote; }
    public ObjectProperty<LocalDate> fechaEntradaProperty() { return fechaEntrada; }
    public ObjectProperty<LocalDate> fechaVencimientoProperty() { return fechaVencimiento; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public BooleanProperty activoProperty() { return activo; }

    // Getters
    public Integer getIdLote() { return idLote.get(); }
    public Integer getIdEntrada() { return idEntrada.get(); }
    public ProductoFX getProducto() { return producto.get(); }
    public Integer getIdProducto() { return idProducto.get(); }
    public String getNumeroLote() { return numeroLote.get(); }
    public LocalDate getFechaEntrada() { return fechaEntrada.get(); }
    public LocalDate getFechaVencimiento() { return fechaVencimiento.get(); }
    public Integer getCantidad() { return cantidad.get(); }
    public Boolean getActivo() { return activo.get(); }

    // Método para obtener fechas como Date para la API
    public Date getFechaEntradaAsDate() {
        return fechaEntrada.get() != null ?
                Date.from(fechaEntrada.get().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }

    public Date getFechaVencimientoAsDate() {
        return fechaVencimiento.get() != null ?
                Date.from(fechaVencimiento.get().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }

    // Setters
    public void setIdLote(Integer idLote) { this.idLote.set(idLote); }
    public void setIdEntrada(Integer idEntrada) { this.idEntrada.set(idEntrada); }
    public void setProducto(ProductoFX producto) { this.producto.set(producto); }
    public void setIdProducto(Integer idProducto) { this.idProducto.set(idProducto); }
    public void setNumeroLote(String numeroLote) { this.numeroLote.set(numeroLote); }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada.set(fechaEntrada); }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento.set(fechaVencimiento); }
    public void setCantidad(Integer cantidad) { this.cantidad.set(cantidad); }
    public void setActivo(Boolean activo) { this.activo.set(activo); }
}