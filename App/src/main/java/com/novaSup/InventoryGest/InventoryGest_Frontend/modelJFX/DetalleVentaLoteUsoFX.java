package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetalleVentaLoteUsoFX {
    private final IntegerProperty idLote;
    private final StringProperty codigoLote;
    private final IntegerProperty cantidadTomada;

    public DetalleVentaLoteUsoFX(Integer idLote, String codigoLote, Integer cantidadTomada) {
        this.idLote = new SimpleIntegerProperty(idLote);
        this.codigoLote = new SimpleStringProperty(codigoLote);
        this.cantidadTomada = new SimpleIntegerProperty(cantidadTomada);
    }

    // Getters para los valores (necesarios para deserialización JSON y uso general)
    public Integer getIdLote() { return idLote.get(); }
    public String getCodigoLote() { return codigoLote.get(); }
    public Integer getCantidadTomada() { return cantidadTomada.get(); }

    // Getters para las propiedades JavaFX (para binding en la UI)
    public IntegerProperty idLoteProperty() { return idLote; }
    public StringProperty codigoLoteProperty() { return codigoLote; }
    public IntegerProperty cantidadTomadaProperty() { return cantidadTomada; }

    // Setters (necesarios si se crean objetos vacíos y se pueblan después, por ejemplo, por librerías JSON)
    // O si se permite la modificación desde la UI.
    public void setIdLote(Integer idLote) { this.idLote.set(idLote); }
    public void setCodigoLote(String codigoLote) { this.codigoLote.set(codigoLote); }
    public void setCantidadTomada(Integer cantidadTomada) { this.cantidadTomada.set(cantidadTomada); }

    // Constructor sin argumentos (útil para algunas librerías de deserialización JSON como Jackson)
    public DetalleVentaLoteUsoFX() {
        this.idLote = new SimpleIntegerProperty();
        this.codigoLote = new SimpleStringProperty();
        this.cantidadTomada = new SimpleIntegerProperty();
    }
} 