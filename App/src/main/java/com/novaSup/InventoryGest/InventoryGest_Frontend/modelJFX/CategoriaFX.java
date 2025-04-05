package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

public class CategoriaFX {
    private final IntegerProperty idCategoria;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final BooleanProperty estado;

    public CategoriaFX(Integer idCategoria, String nombre, String descripcion, Boolean estado) {
        this.idCategoria = new SimpleIntegerProperty(idCategoria != null ? idCategoria : 0);
        this.nombre = new SimpleStringProperty(nombre != null ? nombre : "");
        this.descripcion = new SimpleStringProperty(descripcion != null ? descripcion : "");
        this.estado = new SimpleBooleanProperty(estado != null ? estado : true);
    }

    // Propiedades JavaFX
    public IntegerProperty idCategoriaProperty() { return idCategoria; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty descripcionProperty() { return descripcion; }
    public BooleanProperty estadoProperty() { return estado; }

    // Getters
    public Integer getIdCategoria() { return idCategoria.get(); }
    public String getNombre() { return nombre.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public Boolean getEstado() { return estado.get(); }

    @Override
    public String toString() {
        return nombre.get();
    }
}