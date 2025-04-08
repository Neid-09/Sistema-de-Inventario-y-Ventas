package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class NotificacionFX {
    private final IntegerProperty idNotificacion;
    private final IntegerProperty idUsuario;
    private final StringProperty titulo;
    private final StringProperty mensaje;
    private final ObjectProperty<LocalDateTime> fecha;
    private final BooleanProperty leida;
    private final StringProperty tipo;
    private final IntegerProperty idReferencia;

    public NotificacionFX(Integer idNotificacion, Integer idUsuario, String titulo,
                          String mensaje, LocalDateTime fecha, Boolean leida,
                          String tipo, Integer idReferencia) {
        this.idNotificacion = new SimpleIntegerProperty(idNotificacion);
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.titulo = new SimpleStringProperty(titulo);
        this.mensaje = new SimpleStringProperty(mensaje);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.leida = new SimpleBooleanProperty(leida);
        this.tipo = new SimpleStringProperty(tipo);
        this.idReferencia = new SimpleIntegerProperty(idReferencia);
    }

    // Getters y setters
    public Integer getIdNotificacion() { return idNotificacion.get(); }
    public IntegerProperty idNotificacionProperty() { return idNotificacion; }
    public void setIdNotificacion(Integer id) { idNotificacion.set(id); }

    public Integer getIdUsuario() { return idUsuario.get(); }
    public IntegerProperty idUsuarioProperty() { return idUsuario; }
    public void setIdUsuario(Integer id) { idUsuario.set(id); }

    public String getTitulo() { return titulo.get(); }
    public StringProperty tituloProperty() { return titulo; }
    public void setTitulo(String value) { titulo.set(value); }

    public String getMensaje() { return mensaje.get(); }
    public StringProperty mensajeProperty() { return mensaje; }
    public void setMensaje(String value) { mensaje.set(value); }

    public LocalDateTime getFecha() { return fecha.get(); }
    public ObjectProperty<LocalDateTime> fechaProperty() { return fecha; }
    public void setFecha(LocalDateTime value) { fecha.set(value); }

    public Boolean getLeida() { return leida.get(); }
    public BooleanProperty leidaProperty() { return leida; }
    public void setLeida(Boolean value) { leida.set(value); }

    public String getTipo() { return tipo.get(); }
    public StringProperty tipoProperty() { return tipo; }
    public void setTipo(String value) { tipo.set(value); }

    public Integer getIdReferencia() { return idReferencia.get(); }
    public IntegerProperty idReferenciaProperty() { return idReferencia; }
    public void setIdReferencia(Integer id) { idReferencia.set(id); }
}