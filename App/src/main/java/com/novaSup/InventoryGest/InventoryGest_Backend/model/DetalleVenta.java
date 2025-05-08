package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detalle_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @OneToMany(mappedBy = "detalleVenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleVentaLoteUso> detalleLotesUsados = new ArrayList<>();

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_original", precision = 10, scale = 2, nullable = true)
    private BigDecimal precioUnitarioOriginal;

    @Column(name = "id_promocion_aplicada", nullable = true)
    private Integer idPromocionAplicada;

    @Column(name = "precio_unitario_final", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitarioFinal;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "costo_unitario_producto", precision = 10, scale = 2, nullable = true)
    private BigDecimal costoUnitarioProducto;

    @Column(name = "ganancia_detalle", precision = 12, scale = 2)
    private BigDecimal gananciaDetalle;

    @Column(name = "fecha_creacion", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp fechaCreacion;

    // Lombok genera getters, setters, etc.
}