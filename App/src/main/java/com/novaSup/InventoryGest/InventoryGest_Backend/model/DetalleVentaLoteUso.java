package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_venta_lote_uso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaLoteUso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta_lote_uso")
    private Long id; // Usar Long para IDs es una buena práctica

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_venta", nullable = false)
    private DetalleVenta detalleVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote lote;

    @Column(name = "cantidad_tomada", nullable = false)
    private Integer cantidadTomada;

    // Podrías añadir más campos si son necesarios, como:
    // @Column(name = "costo_unitario_en_venta", precision = 10, scale = 2)
    // private java.math.BigDecimal costoUnitarioEnVenta; // Si el costo del lote en ese momento es relevante
} 