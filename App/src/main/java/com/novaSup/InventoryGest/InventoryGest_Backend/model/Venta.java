package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List; // Para la relación con DetalleVenta si se activa

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    // Relación con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente") // FK en 'ventas' que referencia PK 'id_cliente' en 'clientes'
    private Cliente cliente; 

    // Relación con Vendedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor", referencedColumnName = "id_vendedor") // FK en 'ventas' que referencia PK 'id_vendedor' en 'vendedores' (asumiendo nombre de tabla y PK)
    private Vendedor vendedor;

    @Column(name = "numero_venta", length = 50)
    private String numeroVenta;

    @Column(name = "fecha")
    private Timestamp fecha;

    @Column(name = "requiere_factura")
    private Boolean requiereFactura;

    @Column(name = "aplicar_impuestos")
    private Boolean aplicarImpuestos;

    @Column(name = "tipo_pago", length = 50)
    private String tipoPago;

    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_impuestos", precision = 12, scale = 2)
    private BigDecimal totalImpuestos;

    @Column(name = "total_con_impuestos", precision = 12, scale = 2)
    private BigDecimal totalConImpuestos;

    @Column(name = "estado_venta", length = 50)
    private String estadoVenta;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fecha_creacion", updatable = false)
    private Timestamp fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detallesVenta;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = new Timestamp(System.currentTimeMillis());
        fechaActualizacion = new Timestamp(System.currentTimeMillis());
        if (this.subtotal == null) this.subtotal = BigDecimal.ZERO;
        if (this.totalImpuestos == null) this.totalImpuestos = BigDecimal.ZERO;
        if (this.totalConImpuestos == null) this.totalConImpuestos = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = new Timestamp(System.currentTimeMillis());
    }
}