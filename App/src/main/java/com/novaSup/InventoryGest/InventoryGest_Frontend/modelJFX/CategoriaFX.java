package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

public class CategoriaFX {
    private final IntegerProperty idCategoria;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final BooleanProperty estado;
    private final IntegerProperty duracionGarantia;

    public CategoriaFX(Integer idCategoria, String nombre, String descripcion, Boolean estado, Integer duracionGarantia) {
        this.idCategoria = new SimpleIntegerProperty(idCategoria != null ? idCategoria : 0);
        this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
        this.descripcion = new SimpleStringProperty(descripcion != null ? descripcion : "");
        this.estado = new SimpleBooleanProperty(estado != null ? estado : true);
        this.duracionGarantia = new SimpleIntegerProperty(duracionGarantia != null ? duracionGarantia : 0);
    }

    // Constructor sin parámetros para crear nuevas categorías
    public CategoriaFX() {
        this(0, "", "", true, 0);
    }

    // Propiedades JavaFX
    public IntegerProperty idCategoriaProperty() { return idCategoria; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty descripcionProperty() { return descripcion; }
    public BooleanProperty estadoProperty() { return estado; }
    public IntegerProperty duracionGarantiaProperty() { return duracionGarantia; }

    // Getters
    public Integer getIdCategoria() { return idCategoria.get(); }
    public String getNombre() { return nombre.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public Boolean getEstado() { return estado.get(); }
    public Integer getDuracionGarantia() { return duracionGarantia.get(); }

    // Setters
    public void setIdCategoria(Integer id) { this.idCategoria.set(id); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public void setEstado(Boolean estado) { this.estado.set(estado); }
    public void setDuracionGarantia(Integer duracion) { this.duracionGarantia.set(duracion); }

    @Override
    public String toString() {
        return nombre.get();
    }
}