package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import com.fasterxml.jackson.annotation.JsonInclude;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Si usas LocalDate para las fechas, necesitarás este import
// import java.time.LocalDate;

public class TasaImpuestoFX {

    private final IntegerProperty idTasa;
    private final ObjectProperty<TipoImpuestoFX> tipoImpuesto; // Referencia al objeto TipoImpuestoFX
    private final IntegerProperty tipoImpuestoId; // Puede ser útil si a veces solo tienes el ID
    private final DoubleProperty tasa;
    private final StringProperty fechaInicio; // Considerar ObjectProperty<LocalDate> para manejo avanzado de fechas
    private final StringProperty fechaFin;    // Considerar ObjectProperty<LocalDate>
    private final StringProperty descripcion;

    public TasaImpuestoFX(Integer idTasa, TipoImpuestoFX tipoImpuesto, Integer tipoImpuestoId, Double tasa, String fechaInicio, String fechaFin, String descripcion) {
        this.idTasa = new SimpleIntegerProperty(idTasa == null ? 0 : idTasa);
        this.tipoImpuesto = new SimpleObjectProperty<>(tipoImpuesto);
        this.tipoImpuestoId = new SimpleIntegerProperty(tipoImpuestoId == null ? (tipoImpuesto != null ? tipoImpuesto.getIdTipoImpuesto() : 0) : tipoImpuestoId);
        this.tasa = new SimpleDoubleProperty(tasa == null ? 0.0 : tasa);
        this.fechaInicio = new SimpleStringProperty(fechaInicio);
        this.fechaFin = new SimpleStringProperty(fechaFin);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    public TasaImpuestoFX() {
        this.idTasa = new SimpleIntegerProperty();
        this.tipoImpuesto = new SimpleObjectProperty<>();
        this.tipoImpuestoId = new SimpleIntegerProperty();
        this.tasa = new SimpleDoubleProperty();
        this.fechaInicio = new SimpleStringProperty();
        this.fechaFin = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
    }

    // idTasa
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getIdTasa() {
        return idTasa.get();
    }
    public void setIdTasa(int idTasa) {
        this.idTasa.set(idTasa);
    }
    public IntegerProperty idTasaProperty() {
        return idTasa;
    }

    // tipoImpuesto
    public TipoImpuestoFX getTipoImpuesto() {
        return tipoImpuesto.get();
    }
    public void setTipoImpuesto(TipoImpuestoFX tipoImpuesto) {
        this.tipoImpuesto.set(tipoImpuesto);
        if (tipoImpuesto != null) {
            this.setTipoImpuestoId(tipoImpuesto.getIdTipoImpuesto());
        }
    }
    public ObjectProperty<TipoImpuestoFX> tipoImpuestoProperty() {
        return tipoImpuesto;
    }

    // tipoImpuestoId
    public int getTipoImpuestoId() {
        return tipoImpuestoId.get();
    }
    public void setTipoImpuestoId(int tipoImpuestoId) {
        this.tipoImpuestoId.set(tipoImpuestoId);
    }
    public IntegerProperty tipoImpuestoIdProperty() {
        return tipoImpuestoId;
    }

    // tasa
    public double getTasa() {
        return tasa.get();
    }
    public void setTasa(double tasa) {
        this.tasa.set(tasa);
    }
    public DoubleProperty tasaProperty() {
        return tasa;
    }

    // fechaInicio
    public String getFechaInicio() {
        return fechaInicio.get();
    }
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio.set(fechaInicio);
    }
    public StringProperty fechaInicioProperty() {
        return fechaInicio;
    }

    // fechaFin
    public String getFechaFin() {
        return fechaFin.get();
    }
    public void setFechaFin(String fechaFin) {
        this.fechaFin.set(fechaFin);
    }
    public StringProperty fechaFinProperty() {
        return fechaFin;
    }

    // descripcion
    public String getDescripcion() {
        return descripcion.get();
    }
    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    @Override
    public String toString() {
        return (getTipoImpuesto() != null ? getTipoImpuesto().getNombre() + " - " : "") + getTasa() + "%";
    }
} 