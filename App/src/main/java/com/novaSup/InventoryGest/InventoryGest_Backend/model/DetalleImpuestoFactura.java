package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_impuestos_factura")
public class DetalleImpuestoFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    private Factura factura;

    // Asumiendo que la entidad TipoImpuesto tiene una PK Long idTipoImpuesto y existe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_impuesto", nullable = false) // Asegúrate que TipoImpuesto tiene "id_tipo_impuesto" como nombre de columna para su PK
    private TipoImpuesto tipoImpuesto;

    @Column(name = "base_imponible", precision = 10, scale = 2, nullable = false)
    private BigDecimal baseImponible;

    @Column(name = "tasa_aplicada", precision = 5, scale = 2, nullable = false)
    private BigDecimal tasaAplicada;

    @Column(name = "monto_impuesto", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoImpuesto;

    // Getters y Setters

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public BigDecimal getTasaAplicada() {
        return tasaAplicada;
    }

    public void setTasaAplicada(BigDecimal tasaAplicada) {
        this.tasaAplicada = tasaAplicada;
    }

    public BigDecimal getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(BigDecimal montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }
} 