package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "comisiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comision")
    private Integer idComision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @Column(name = "monto_venta", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoVenta;

    @Column(name = "porcentaje_comision", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeComision;

    @Column(name = "monto_comision", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoComision;

    @Column(name = "fecha_calculo", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fechaCalculo;

    @Column(length = 50, nullable = false)
    private String estado; // Ej: "PENDIENTE", "PAGADO"

    // Lombok genera getters, setters, etc.

    @PrePersist
    protected void onCreate() {
        if (fechaCalculo == null) { // Establecer la fecha si no se proporciona
            fechaCalculo = new Timestamp(System.currentTimeMillis());
        }
    }
}
