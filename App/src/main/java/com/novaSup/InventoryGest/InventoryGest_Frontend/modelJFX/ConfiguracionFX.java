package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfiguracionFX {
    private final SimpleIntegerProperty idConfig;
    private final StringProperty clave = new SimpleStringProperty();
    private final StringProperty valor = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();

    public ConfiguracionFX() {
        this(null, "", "", "");
    }

    public ConfiguracionFX(Integer idConfig, String clave, String valor, String descripcion) {
        // Inicializar idConfig como objeto que puede ser nulo
        this.idConfig = new SimpleIntegerProperty(this, "idConfig",
                idConfig != null ? idConfig : 0);
        this.clave.set(clave != null ? clave : "");
        this.valor.set(valor != null ? valor : "");
        this.descripcion.set(descripcion != null ? descripcion : "");
    }

    // Getters y setters para IdConfig
    public Integer getIdConfig() {
        int value = idConfig.get();
        // Devuelve null si el valor es 0 (representando un id no asignado)
        return value == 0 ? null : value;
    }

    public void setIdConfig(Integer idConfig) {
        this.idConfig.set(idConfig != null ? idConfig : 0);
    }

    public IntegerProperty idConfigProperty() {
        return idConfig;
    }

    // Getters y setters para Clave
    public String getClave() {
        return clave.get();
    }

    public void setClave(String clave) {
        this.clave.set(clave);
    }

    public StringProperty claveProperty() {
        return clave;
    }

    // Getters y setters para Valor
    public String getValor() {
        return valor.get();
    }

    public void setValor(String valor) {
        this.valor.set(valor);
    }

    public StringProperty valorProperty() {
        return valor;
    }

    // Getters y setters para Descripcion
    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }
}