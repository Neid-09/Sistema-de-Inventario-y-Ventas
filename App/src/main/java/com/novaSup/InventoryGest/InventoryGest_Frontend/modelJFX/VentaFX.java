package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import com.fasterxml.jackson.annotation.JsonFormat; // Importar JsonFormat
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.sql.Timestamp; // O convertir a LocalDateTime/String en el servicio de frontend
import java.time.LocalDateTime;
import java.time.ZoneId; // Para conversión de Timestamp
import java.util.List; // Asegúrate de que esta importación es necesaria y está en el doc.

public class VentaFX {
    private final IntegerProperty idVenta = new SimpleIntegerProperty();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") // Especificar el formato de fecha y hora
    private final ObjectProperty<LocalDateTime> fecha = new SimpleObjectProperty<>();
    private final IntegerProperty idCliente = new SimpleIntegerProperty();
    private final StringProperty nombreCliente = new SimpleStringProperty();
    private final IntegerProperty idVendedor = new SimpleIntegerProperty();
    private final StringProperty nombreVendedor = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> total = new SimpleObjectProperty<>(); //Total con impuestos
    private final BooleanProperty requiereFactura = new SimpleBooleanProperty();
    private final StringProperty numeroVenta = new SimpleStringProperty();
    private final BooleanProperty aplicarImpuestos = new SimpleBooleanProperty();
    private final StringProperty tipoPago = new SimpleStringProperty(); // Asegurar que exista en el DTO backend
    private final ObjectProperty<BigDecimal> totalImpuestos = new SimpleObjectProperty<>(); // Valor total de impuestos(sin sumar al total final)
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>(); // Total sin impuestos
    private final ListProperty<DetalleVentaFX> detalles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty cantidadTotalProductos = new SimpleIntegerProperty(); // Nueva propiedad

    // Constructor sin argumentos (para deserialización JSON)
    public VentaFX() {}

    // Constructor completo (ejemplo)
    public VentaFX(Integer idVenta, Timestamp fecha, Integer idCliente, String nombreCliente, 
                   Integer idVendedor, String nombreVendedor, BigDecimal total, 
                   boolean requiereFactura, String numeroVenta, boolean aplicarImpuestos, 
                   String tipoPago, List<DetalleVentaFX> detalles,
                   BigDecimal totalImpuestos, BigDecimal subtotal) { // Nuevos parámetros
        this.idVenta.set(idVenta);
        if (fecha != null) { // Convertir Timestamp a LocalDateTime directamente
            this.fecha.set(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } else {
            this.fecha.set(null);
        }
        this.idCliente.set(idCliente);
        this.nombreCliente.set(nombreCliente);
        this.idVendedor.set(idVendedor);
        this.nombreVendedor.set(nombreVendedor);
        this.total.set(total);
        this.requiereFactura.set(requiereFactura);
        this.numeroVenta.set(numeroVenta);
        this.aplicarImpuestos.set(aplicarImpuestos);
        this.tipoPago.set(tipoPago);
        this.totalImpuestos.set(totalImpuestos); // Asignar nuevo campo
        this.subtotal.set(subtotal); // Asignar nuevo campo
        this.detalles.set(FXCollections.observableArrayList(detalles));
        this.cantidadTotalProductos.set(calcularCantidadTotalProductos(detalles)); // Calcular al construir
    }

    // Método para calcular la cantidad total de productos
    private int calcularCantidadTotalProductos(List<DetalleVentaFX> detalles) {
        if (detalles == null) {
            return 0;
        }
        return detalles.stream().mapToInt(DetalleVentaFX::getCantidad).sum();
    }

    // Getters para valores y Setters (importantes para la deserialización JSON)
    public Integer getIdVenta() { return idVenta.get(); }
    public void setIdVenta(Integer idVenta) { this.idVenta.set(idVenta); }

    public LocalDateTime getFecha() { return fecha.get(); }
    public void setFecha(LocalDateTime localDateTime) { this.fecha.set(localDateTime); }

    public Integer getIdCliente() { return idCliente.get(); }
    public void setIdCliente(Integer idCliente) { this.idCliente.set(idCliente); }

    public String getNombreCliente() { return nombreCliente.get(); }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente.set(nombreCliente); }
    
    public Integer getIdVendedor() { return idVendedor.get(); }
    public void setIdVendedor(Integer idVendedor) { this.idVendedor.set(idVendedor); }

    public String getNombreVendedor() { return nombreVendedor.get(); }
    public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor.set(nombreVendedor); }

    public BigDecimal getTotal() { return total.get(); }
    public void setTotal(BigDecimal total) { this.total.set(total); }

    public boolean isRequiereFactura() { return requiereFactura.get(); }
    public void setRequiereFactura(boolean requiereFactura) { this.requiereFactura.set(requiereFactura); }

    public String getNumeroVenta() { return numeroVenta.get(); }
    public void setNumeroVenta(String numeroVenta) { this.numeroVenta.set(numeroVenta); }

    public boolean isAplicarImpuestos() { return aplicarImpuestos.get(); }
    public void setAplicarImpuestos(boolean aplicarImpuestos) { this.aplicarImpuestos.set(aplicarImpuestos); }
    
    public String getTipoPago() { return tipoPago.get(); }
    public void setTipoPago(String tipoPago) { this.tipoPago.set(tipoPago); }

    public BigDecimal getTotalImpuestos() { return totalImpuestos.get(); } // Getter para totalImpuestos
    public void setTotalImpuestos(BigDecimal totalImpuestos) { this.totalImpuestos.set(totalImpuestos); } // Setter para totalImpuestos

    public BigDecimal getSubtotal() { return subtotal.get(); } // Getter para subtotal
    public void setSubtotal(BigDecimal subtotal) { this.subtotal.set(subtotal); } // Setter para subtotal

    public ObservableList<DetalleVentaFX> getDetalles() { return detalles.get(); }
    public void setDetalles(List<DetalleVentaFX> detalles) { 
        this.detalles.set(FXCollections.observableArrayList(detalles));
        this.cantidadTotalProductos.set(calcularCantidadTotalProductos(detalles)); // Recalcular al modificar detalles
    }

    public Integer getCantidadTotalProductos() { return cantidadTotalProductos.get(); }
    public void setCantidadTotalProductos(Integer cantidadTotalProductos) { this.cantidadTotalProductos.set(cantidadTotalProductos); }


    // Getters para propiedades JavaFX (para binding)
    public IntegerProperty idVentaProperty() { return idVenta; }
    public ObjectProperty<LocalDateTime> fechaProperty() { return fecha; }
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty nombreClienteProperty() { return nombreCliente; }
    public IntegerProperty idVendedorProperty() { return idVendedor; }
    public StringProperty nombreVendedorProperty() { return nombreVendedor; }
    public ObjectProperty<BigDecimal> totalProperty() { return total; }
    public BooleanProperty requiereFacturaProperty() { return requiereFactura; }
    public StringProperty numeroVentaProperty() { return numeroVenta; }
    public BooleanProperty aplicarImpuestosProperty() { return aplicarImpuestos; }
    public StringProperty tipoPagoProperty() { return tipoPago; }
    public ObjectProperty<BigDecimal> totalImpuestosProperty() { return totalImpuestos; } // Property getter para totalImpuestos
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; } // Property getter para subtotal
    public ListProperty<DetalleVentaFX> detallesProperty() { return detalles; }
    public IntegerProperty cantidadTotalProductosProperty() { return cantidadTotalProductos; } // Nueva propiedad
}