package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo JavaFX para representar un vendedor.
 * Utiliza properties de JavaFX para binding automático con la interfaz de usuario.
 */
public class VendedorFX {
    private final IntegerProperty idVendedor;
    private final IntegerProperty idUsuario;
    private final ObjectProperty<UsuarioSimplifiedFX> usuario;
    private final ObjectProperty<BigDecimal> objetivoVentas;
    private final ObjectProperty<LocalDate> fechaContratacion;

    /**
     * Constructor por defecto.
     */
    public VendedorFX() {
        this.idVendedor = new SimpleIntegerProperty(this, "idVendedor");
        this.idUsuario = new SimpleIntegerProperty(this, "idUsuario");
        this.usuario = new SimpleObjectProperty<>(this, "usuario");
        this.objetivoVentas = new SimpleObjectProperty<>(this, "objetivoVentas");
        this.fechaContratacion = new SimpleObjectProperty<>(this, "fechaContratacion");
    }

    /**
     * Constructor con parámetros.
     */
    public VendedorFX(Integer idVendedor, Integer idUsuario, BigDecimal objetivoVentas, LocalDate fechaContratacion) {
        this();
        setIdVendedor(idVendedor);
        setIdUsuario(idUsuario);
        setObjetivoVentas(objetivoVentas);
        setFechaContratacion(fechaContratacion);
    }

    /**
     * Constructor con información del usuario.
     */
    public VendedorFX(Integer idVendedor, Integer idUsuario, UsuarioSimplifiedFX usuario, 
                      BigDecimal objetivoVentas, LocalDate fechaContratacion) {
        this(idVendedor, idUsuario, objetivoVentas, fechaContratacion);
        setUsuario(usuario);
    }

    // Propiedades y métodos para idVendedor
    public IntegerProperty idVendedorProperty() {
        return idVendedor;
    }

    public int getIdVendedor() {
        return idVendedor.get();
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor.set(idVendedor != null ? idVendedor : 0);
    }

    // Propiedades y métodos para idUsuario
    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario.get();
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario.set(idUsuario != null ? idUsuario : 0);
    }

    // Propiedades y métodos para usuario
    public ObjectProperty<UsuarioSimplifiedFX> usuarioProperty() {
        return usuario;
    }

    public UsuarioSimplifiedFX getUsuario() {
        return usuario.get();
    }

    public void setUsuario(UsuarioSimplifiedFX usuario) {
        this.usuario.set(usuario);
    }

    // Propiedades y métodos para objetivoVentas
    public ObjectProperty<BigDecimal> objetivoVentasProperty() {
        return objetivoVentas;
    }

    public BigDecimal getObjetivoVentas() {
        return objetivoVentas.get();
    }

    public void setObjetivoVentas(BigDecimal objetivoVentas) {
        this.objetivoVentas.set(objetivoVentas);
    }

    // Propiedades y métodos para fechaContratacion
    public ObjectProperty<LocalDate> fechaContratacionProperty() {
        return fechaContratacion;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion.get();
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion.set(fechaContratacion);
    }

    // Métodos adicionales para facilitar el uso
    
    /**
     * Obtiene el nombre del usuario asociado.
     * @return Nombre del usuario o cadena vacía si no hay usuario asociado
     */
    public String getNombreUsuario() {
        return usuario.get() != null ? usuario.get().getNombre() : "";
    }

    /**
     * Verifica si el vendedor tiene un usuario asociado.
     * @return true si tiene usuario asociado
     */
    public boolean tieneUsuarioAsociado() {
        return usuario.get() != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VendedorFX{");
        sb.append("idVendedor=").append(getIdVendedor());
        sb.append(", idUsuario=").append(getIdUsuario());
        if (tieneUsuarioAsociado()) {
            sb.append(", nombreUsuario='").append(getNombreUsuario()).append("'");
        }
        sb.append(", objetivoVentas=").append(getObjetivoVentas());
        sb.append(", fechaContratacion=").append(getFechaContratacion());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VendedorFX that = (VendedorFX) obj;
        return getIdVendedor() == that.getIdVendedor();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getIdVendedor());
    }
}
