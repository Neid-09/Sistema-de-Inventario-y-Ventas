package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Modelo JavaFX para representar un Cliente en la interfaz de usuario.
 * Utiliza propiedades de JavaFX para facilitar el binding con los controles de la UI.
 */
public class ClienteFX {
    private final IntegerProperty idCliente;
    private final StringProperty cedula;
    private final StringProperty nombre; // Ya no hay apellido separado
    private final StringProperty correo;
    private final StringProperty celular; // Renombrado de telefono
    private final StringProperty direccion;
    private final IntegerProperty puntosFidelidad;
    private final ObjectProperty<BigDecimal> limiteCredito; // Usar BigDecimal para precisión monetaria
    private final ObjectProperty<BigDecimal> totalComprado;
    private final ObjectProperty<OffsetDateTime> ultimaCompra; // Cambiado a LocalDateTime
    private final BooleanProperty tieneCreditos; // Nueva propiedad

    // Constructor por defecto
    public ClienteFX() {
        this.idCliente = new SimpleIntegerProperty(0); // Inicializar con 0 o null según la lógica
        this.cedula = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.celular = new SimpleStringProperty(""); // Renombrado
        this.direccion = new SimpleStringProperty("");
        this.puntosFidelidad = new SimpleIntegerProperty(0);
        this.limiteCredito = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.totalComprado = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.ultimaCompra = new SimpleObjectProperty<>(null); // Inicializar con null
        this.tieneCreditos = new SimpleBooleanProperty(false); // Inicializar como false
    }

    // Constructor con parámetros
    public ClienteFX(Integer idCliente, String cedula, String nombre, String correo,
                     String celular, String direccion, Integer puntosFidelidad, BigDecimal limiteCredito,
                     BigDecimal totalComprado, OffsetDateTime ultimaCompra, Boolean tieneCreditos) { // Acepta LocalDateTime y Boolean
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.cedula = new SimpleStringProperty(cedula);
        this.nombre = new SimpleStringProperty(nombre);
        this.correo = new SimpleStringProperty(correo);
        this.celular = new SimpleStringProperty(celular); // Renombrado
        this.direccion = new SimpleStringProperty(direccion);
        this.puntosFidelidad = new SimpleIntegerProperty(puntosFidelidad);
        this.limiteCredito = new SimpleObjectProperty<>(limiteCredito);
        this.totalComprado = new SimpleObjectProperty<>(totalComprado);
        this.ultimaCompra = new SimpleObjectProperty<>(ultimaCompra); // Asigna LocalDateTime
        this.tieneCreditos = new SimpleBooleanProperty(tieneCreditos != null && tieneCreditos); // Manejar null
    }

    // --- Getters ---
    public Integer getIdCliente() { return idCliente.get(); }
    public String getCedula() { return cedula.get(); }
    public String getNombre() { return nombre.get(); }
    public String getCorreo() { return correo.get(); }
    public String getCelular() { return celular.get(); } // Renombrado
    public String getDireccion() { return direccion.get(); }
    public Integer getPuntosFidelidad() { return puntosFidelidad.get(); }
    public BigDecimal getLimiteCredito() { return limiteCredito.get(); }
    public BigDecimal getTotalComprado() { return totalComprado.get(); }
    public OffsetDateTime getUltimaCompra() { return ultimaCompra.get(); } // Devuelve LocalDateTime
    public boolean isTieneCreditos() { return tieneCreditos.get(); } // Getter para boolean

    // --- Setters ---
    public void setIdCliente(Integer idCliente) { this.idCliente.set(idCliente); }
    public void setCedula(String cedula) { this.cedula.set(cedula); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setCorreo(String correo) { this.correo.set(correo); }
    public void setCelular(String celular) { this.celular.set(celular); } // Renombrado
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public void setPuntosFidelidad(Integer puntosFidelidad) { this.puntosFidelidad.set(puntosFidelidad); }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito.set(limiteCredito); }
    public void setTotalComprado(BigDecimal totalComprado) { this.totalComprado.set(totalComprado); }
    public void setUltimaCompra(OffsetDateTime ultimaCompra) { this.ultimaCompra.set(ultimaCompra); } // Acepta LocalDateTime
    public void setTieneCreditos(boolean tieneCreditos) { this.tieneCreditos.set(tieneCreditos); } // Setter para boolean

    // --- Property Getters ---
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty cedulaProperty() { return cedula; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty correoProperty() { return correo; }
    public StringProperty celularProperty() { return celular; } // Renombrado
    public StringProperty direccionProperty() { return direccion; }
    public IntegerProperty puntosFidelidadProperty() { return puntosFidelidad; }
    public ObjectProperty<BigDecimal> limiteCreditoProperty() { return limiteCredito; }
    public ObjectProperty<BigDecimal> totalCompradoProperty() { return totalComprado; }
    public ObjectProperty<OffsetDateTime> ultimaCompraProperty() { return ultimaCompra; } // Devuelve ObjectProperty<LocalDateTime>
    public BooleanProperty tieneCreditosProperty() { return tieneCreditos; } // Property getter para boolean
}
