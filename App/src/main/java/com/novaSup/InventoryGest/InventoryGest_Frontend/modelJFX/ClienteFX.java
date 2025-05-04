package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo JavaFX para representar un Cliente en la interfaz de usuario.
 * Utiliza propiedades de JavaFX para facilitar el binding con los controles de la UI.
 */
public class ClienteFX {
    private final IntegerProperty idCliente;
    private final StringProperty cedula;
    private final StringProperty nombre;
    private final StringProperty apellido;
    private final StringProperty correo;
    private final StringProperty telefono;
    private final StringProperty direccion;
    private final IntegerProperty puntosFidelidad;
    private final ObjectProperty<BigDecimal> limiteCredito; // Usar BigDecimal para precisión monetaria
    private final ObjectProperty<BigDecimal> totalComprado;
    private final ObjectProperty<LocalDate> ultimaCompra; // Usar LocalDate para fechas

    // Constructor por defecto
    public ClienteFX() {
        this.idCliente = new SimpleIntegerProperty(0); // Inicializar con 0 o null según la lógica
        this.cedula = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.apellido = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.telefono = new SimpleStringProperty("");
        this.direccion = new SimpleStringProperty("");
        this.puntosFidelidad = new SimpleIntegerProperty(0);
        this.limiteCredito = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.totalComprado = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.ultimaCompra = new SimpleObjectProperty<>(null);
    }

    // Constructor con parámetros
    public ClienteFX(Integer idCliente, String cedula, String nombre, String apellido, String correo,
                     String telefono, String direccion, Integer puntosFidelidad, BigDecimal limiteCredito,
                     BigDecimal totalComprado, LocalDate ultimaCompra) {
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.cedula = new SimpleStringProperty(cedula);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.correo = new SimpleStringProperty(correo);
        this.telefono = new SimpleStringProperty(telefono);
        this.direccion = new SimpleStringProperty(direccion);
        this.puntosFidelidad = new SimpleIntegerProperty(puntosFidelidad);
        this.limiteCredito = new SimpleObjectProperty<>(limiteCredito);
        this.totalComprado = new SimpleObjectProperty<>(totalComprado);
        this.ultimaCompra = new SimpleObjectProperty<>(ultimaCompra);
    }

    // --- Getters ---
    public Integer getIdCliente() { return idCliente.get(); }
    public String getCedula() { return cedula.get(); }
    public String getNombre() { return nombre.get(); }
    public String getApellido() { return apellido.get(); }
    public String getCorreo() { return correo.get(); }
    public String getTelefono() { return telefono.get(); }
    public String getDireccion() { return direccion.get(); }
    public Integer getPuntosFidelidad() { return puntosFidelidad.get(); }
    public BigDecimal getLimiteCredito() { return limiteCredito.get(); }
    public BigDecimal getTotalComprado() { return totalComprado.get(); }
    public LocalDate getUltimaCompra() { return ultimaCompra.get(); }

    // --- Setters ---
    public void setIdCliente(Integer idCliente) { this.idCliente.set(idCliente); }
    public void setCedula(String cedula) { this.cedula.set(cedula); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setApellido(String apellido) { this.apellido.set(apellido); }
    public void setCorreo(String correo) { this.correo.set(correo); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public void setPuntosFidelidad(Integer puntosFidelidad) { this.puntosFidelidad.set(puntosFidelidad); }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito.set(limiteCredito); }
    public void setTotalComprado(BigDecimal totalComprado) { this.totalComprado.set(totalComprado); }
    public void setUltimaCompra(LocalDate ultimaCompra) { this.ultimaCompra.set(ultimaCompra); }

    // --- Property Getters ---
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty cedulaProperty() { return cedula; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty apellidoProperty() { return apellido; }
    public StringProperty correoProperty() { return correo; }
    public StringProperty telefonoProperty() { return telefono; }
    public StringProperty direccionProperty() { return direccion; }
    public IntegerProperty puntosFidelidadProperty() { return puntosFidelidad; }
    public ObjectProperty<BigDecimal> limiteCreditoProperty() { return limiteCredito; }
    public ObjectProperty<BigDecimal> totalCompradoProperty() { return totalComprado; }
    public ObjectProperty<LocalDate> ultimaCompraProperty() { return ultimaCompra; }
}
