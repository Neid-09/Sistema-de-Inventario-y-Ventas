package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.util.List; // Asegúrate de que esta importación es necesaria y está en el doc.

public class DetalleVentaFX {
    private final IntegerProperty idDetalleVenta = new SimpleIntegerProperty();
    private final StringProperty codProducto = new SimpleStringProperty();
    private final StringProperty nombreProducto = new SimpleStringProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> precioUnitarioOriginal = new SimpleObjectProperty<>();
    private final IntegerProperty idPromocionAplicada = new SimpleIntegerProperty(); // Usar 0 o un valor indicador si es nulo y se necesita IntegerProperty
    private final ObjectProperty<BigDecimal> precioUnitarioFinal = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>();
    private final ListProperty<DetalleVentaLoteUsoFX> lotesUsados = new SimpleListProperty<>(FXCollections.observableArrayList());

    // Constructor completo (ejemplo)
    public DetalleVentaFX(Integer idDetalleVenta, String codProducto, String nombreProducto, Integer cantidad, BigDecimal precioUnitarioOriginal, Integer idPromocionAplicada, BigDecimal precioUnitarioFinal, BigDecimal subtotal, List<DetalleVentaLoteUsoFX> lotesUsados) {
        this.idDetalleVenta.set(idDetalleVenta);
        this.codProducto.set(codProducto);
        this.nombreProducto.set(nombreProducto);
        this.cantidad.set(cantidad);
        this.precioUnitarioOriginal.set(precioUnitarioOriginal);
        this.idPromocionAplicada.set(idPromocionAplicada != null ? idPromocionAplicada : 0); // Manejo de nulo para IntegerProperty
        this.precioUnitarioFinal.set(precioUnitarioFinal);
        this.subtotal.set(subtotal);
        this.lotesUsados.set(FXCollections.observableArrayList(lotesUsados));
    }
    
    // Constructor sin argumentos (para deserialización JSON)
    public DetalleVentaFX() {}

    // Getters para valores
    public Integer getIdDetalleVenta() { return idDetalleVenta.get(); }
    public void setIdDetalleVenta(Integer idDetalleVenta) { this.idDetalleVenta.set(idDetalleVenta); }

    public String getCodProducto() { return codProducto.get(); }
    public void setCodProducto(String codProducto) { this.codProducto.set(codProducto); }

    public String getNombreProducto() { return nombreProducto.get(); }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto.set(nombreProducto); }

    public Integer getCantidad() { return cantidad.get(); }
    public void setCantidad(Integer cantidad) { this.cantidad.set(cantidad); }

    public BigDecimal getPrecioUnitarioOriginal() { return precioUnitarioOriginal.get(); }
    public void setPrecioUnitarioOriginal(BigDecimal precioUnitarioOriginal) { this.precioUnitarioOriginal.set(precioUnitarioOriginal); }

    public Integer getIdPromocionAplicada() { 
        // Devolver null si el valor es el indicador de nulo (ej. 0)
        return idPromocionAplicada.get() == 0 ? null : idPromocionAplicada.get(); 
    }
    public void setIdPromocionAplicada(Integer idPromocionAplicada) { 
        this.idPromocionAplicada.set(idPromocionAplicada != null ? idPromocionAplicada : 0); // Manejo de nulo para IntegerProperty
    }
    
    public BigDecimal getPrecioUnitarioFinal() { return precioUnitarioFinal.get(); }
    public void setPrecioUnitarioFinal(BigDecimal precio) { this.precioUnitarioFinal.set(precio); }

    public BigDecimal getSubtotal() { return subtotal.get(); }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal.set(subtotal); }

    public ObservableList<DetalleVentaLoteUsoFX> getLotesUsados() { return lotesUsados.get(); }
    public void setLotesUsados(List<DetalleVentaLoteUsoFX> lotes) { this.lotesUsados.set(FXCollections.observableArrayList(lotes));}


    // Getters para propiedades JavaFX (para binding)
    public IntegerProperty idDetalleVentaProperty() { return idDetalleVenta; }
    public StringProperty codProductoProperty() { return codProducto; }
    public StringProperty nombreProductoProperty() { return nombreProducto; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public ObjectProperty<BigDecimal> precioUnitarioOriginalProperty() { return precioUnitarioOriginal; }
    public IntegerProperty idPromocionAplicadaProperty() { return idPromocionAplicada; }
    public ObjectProperty<BigDecimal> precioUnitarioFinalProperty() { return precioUnitarioFinal; }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }
    public ListProperty<DetalleVentaLoteUsoFX> lotesUsadosProperty() { return lotesUsados; }
} 