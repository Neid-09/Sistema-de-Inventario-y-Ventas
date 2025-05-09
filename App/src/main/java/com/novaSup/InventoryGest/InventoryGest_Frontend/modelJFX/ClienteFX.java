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
    private final StringProperty documentoIdentidad; // Mapea a documentoIdentidad del backend
    private final StringProperty nombre;
    private final StringProperty correo;
    private final StringProperty celular;
    private final StringProperty direccion;
    private final IntegerProperty puntosFidelidad;
    private final ObjectProperty<BigDecimal> limiteCredito;
    private final ObjectProperty<BigDecimal> totalComprado;
    private final ObjectProperty<OffsetDateTime> ultimaCompra;
    private final BooleanProperty tieneCreditos; // Existente, se mantiene por ahora

    // Nuevos campos
    private final BooleanProperty requiereFacturaDefault;
    private final StringProperty razonSocial;
    private final StringProperty identificacionFiscal;
    private final StringProperty direccionFacturacion;
    private final StringProperty correoFacturacion;
    private final StringProperty tipoFacturaDefault;
    private final ObjectProperty<OffsetDateTime> fechaRegistro;
    private final ObjectProperty<OffsetDateTime> fechaActualizacion;
    private final BooleanProperty activo;


    // Constructor por defecto
    public ClienteFX() {
        this.idCliente = new SimpleIntegerProperty(0);
        this.documentoIdentidad = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.celular = new SimpleStringProperty("");
        this.direccion = new SimpleStringProperty("");
        this.puntosFidelidad = new SimpleIntegerProperty(0);
        this.limiteCredito = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.totalComprado = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.ultimaCompra = new SimpleObjectProperty<>(null);
        this.tieneCreditos = new SimpleBooleanProperty(false);

        // Inicialización nuevos campos
        this.requiereFacturaDefault = new SimpleBooleanProperty(false);
        this.razonSocial = new SimpleStringProperty("");
        this.identificacionFiscal = new SimpleStringProperty("");
        this.direccionFacturacion = new SimpleStringProperty("");
        this.correoFacturacion = new SimpleStringProperty("");
        this.tipoFacturaDefault = new SimpleStringProperty("");
        this.fechaRegistro = new SimpleObjectProperty<>(null);
        this.fechaActualizacion = new SimpleObjectProperty<>(null);
        this.activo = new SimpleBooleanProperty(true); // Por defecto activo
    }

    // Constructor con parámetros
    public ClienteFX(Integer idCliente, String documentoIdentidad, String nombre, String correo,
                     String celular, String direccion, Integer puntosFidelidad, BigDecimal limiteCredito,
                     BigDecimal totalComprado, OffsetDateTime ultimaCompra, Boolean tieneCreditos,
                     // Nuevos parámetros
                     Boolean requiereFacturaDefault, String razonSocial, String identificacionFiscal,
                     String direccionFacturacion, String correoFacturacion, String tipoFacturaDefault,
                     OffsetDateTime fechaRegistro, OffsetDateTime fechaActualizacion, Boolean activo) {
        this.idCliente = new SimpleIntegerProperty(idCliente != null ? idCliente : 0);
        this.documentoIdentidad = new SimpleStringProperty(documentoIdentidad);
        this.nombre = new SimpleStringProperty(nombre);
        this.correo = new SimpleStringProperty(correo);
        this.celular = new SimpleStringProperty(celular);
        this.direccion = new SimpleStringProperty(direccion);
        this.puntosFidelidad = new SimpleIntegerProperty(puntosFidelidad != null ? puntosFidelidad : 0);
        this.limiteCredito = new SimpleObjectProperty<>(limiteCredito);
        this.totalComprado = new SimpleObjectProperty<>(totalComprado);
        this.ultimaCompra = new SimpleObjectProperty<>(ultimaCompra);
        this.tieneCreditos = new SimpleBooleanProperty(tieneCreditos != null && tieneCreditos);

        // Asignación nuevos campos
        this.requiereFacturaDefault = new SimpleBooleanProperty(requiereFacturaDefault != null && requiereFacturaDefault);
        this.razonSocial = new SimpleStringProperty(razonSocial);
        this.identificacionFiscal = new SimpleStringProperty(identificacionFiscal);
        this.direccionFacturacion = new SimpleStringProperty(direccionFacturacion);
        this.correoFacturacion = new SimpleStringProperty(correoFacturacion);
        this.tipoFacturaDefault = new SimpleStringProperty(tipoFacturaDefault);
        this.fechaRegistro = new SimpleObjectProperty<>(fechaRegistro);
        this.fechaActualizacion = new SimpleObjectProperty<>(fechaActualizacion);
        this.activo = new SimpleBooleanProperty(activo != null && activo);
    }

    // --- Getters ---
    public Integer getIdCliente() { return idCliente.get(); }
    public String getDocumentoIdentidad() { return documentoIdentidad.get(); }
    public String getNombre() { return nombre.get(); }
    public String getCorreo() { return correo.get(); }
    public String getCelular() { return celular.get(); }
    public String getDireccion() { return direccion.get(); }
    public Integer getPuntosFidelidad() { return puntosFidelidad.get(); }
    public BigDecimal getLimiteCredito() { return limiteCredito.get(); }
    public BigDecimal getTotalComprado() { return totalComprado.get(); }
    public OffsetDateTime getUltimaCompra() { return ultimaCompra.get(); }
    public boolean isTieneCreditos() { return tieneCreditos.get(); }
    public boolean isRequiereFacturaDefault() { return requiereFacturaDefault.get(); }
    public String getRazonSocial() { return razonSocial.get(); }
    public String getIdentificacionFiscal() { return identificacionFiscal.get(); }
    public String getDireccionFacturacion() { return direccionFacturacion.get(); }
    public String getCorreoFacturacion() { return correoFacturacion.get(); }
    public String getTipoFacturaDefault() { return tipoFacturaDefault.get(); }
    public OffsetDateTime getFechaRegistro() { return fechaRegistro.get(); }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion.get(); }
    public boolean isActivo() { return activo.get(); }


    // --- Setters ---
    public void setIdCliente(Integer idCliente) { this.idCliente.set(idCliente); }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad.set(documentoIdentidad); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setCorreo(String correo) { this.correo.set(correo); }
    public void setCelular(String celular) { this.celular.set(celular); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public void setPuntosFidelidad(Integer puntosFidelidad) { this.puntosFidelidad.set(puntosFidelidad); }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito.set(limiteCredito); }
    public void setTotalComprado(BigDecimal totalComprado) { this.totalComprado.set(totalComprado); }
    public void setUltimaCompra(OffsetDateTime ultimaCompra) { this.ultimaCompra.set(ultimaCompra); }
    public void setTieneCreditos(boolean tieneCreditos) { this.tieneCreditos.set(tieneCreditos); }
    public void setRequiereFacturaDefault(boolean requiereFacturaDefault) { this.requiereFacturaDefault.set(requiereFacturaDefault); }
    public void setRazonSocial(String razonSocial) { this.razonSocial.set(razonSocial); }
    public void setIdentificacionFiscal(String identificacionFiscal) { this.identificacionFiscal.set(identificacionFiscal); }
    public void setDireccionFacturacion(String direccionFacturacion) { this.direccionFacturacion.set(direccionFacturacion); }
    public void setCorreoFacturacion(String correoFacturacion) { this.correoFacturacion.set(correoFacturacion); }
    public void setTipoFacturaDefault(String tipoFacturaDefault) { this.tipoFacturaDefault.set(tipoFacturaDefault); }
    public void setFechaRegistro(OffsetDateTime fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion.set(fechaActualizacion); }
    public void setActivo(boolean activo) { this.activo.set(activo); }


    // --- Property Getters ---
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty documentoIdentidadProperty() { return documentoIdentidad; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty correoProperty() { return correo; }
    public StringProperty celularProperty() { return celular; }
    public StringProperty direccionProperty() { return direccion; }
    public IntegerProperty puntosFidelidadProperty() { return puntosFidelidad; }
    public ObjectProperty<BigDecimal> limiteCreditoProperty() { return limiteCredito; }
    public ObjectProperty<BigDecimal> totalCompradoProperty() { return totalComprado; }
    public ObjectProperty<OffsetDateTime> ultimaCompraProperty() { return ultimaCompra; }
    public BooleanProperty tieneCreditosProperty() { return tieneCreditos; }
    public BooleanProperty requiereFacturaDefaultProperty() { return requiereFacturaDefault; }
    public StringProperty razonSocialProperty() { return razonSocial; }
    public StringProperty identificacionFiscalProperty() { return identificacionFiscal; }
    public StringProperty direccionFacturacionProperty() { return direccionFacturacion; }
    public StringProperty correoFacturacionProperty() { return correoFacturacion; }
    public StringProperty tipoFacturaDefaultProperty() { return tipoFacturaDefault; }
    public ObjectProperty<OffsetDateTime> fechaRegistroProperty() { return fechaRegistro; }
    public ObjectProperty<OffsetDateTime> fechaActualizacionProperty() { return fechaActualizacion; }
    public BooleanProperty activoProperty() { return activo; }
}
