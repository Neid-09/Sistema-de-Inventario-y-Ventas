package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long idFactura;

    // Asumiendo que la entidad Venta tiene una PK Long idVenta y existe en el mismo paquete o se importa
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", referencedColumnName = "id_venta") // Asegúrate que Venta tiene "id_venta" como nombre de columna para su PK
    private Venta venta;

    @Column(name = "numero_factura", length = 50, nullable = false, unique = true)
    private String numeroFactura;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "total_impuestos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalImpuestos;

    @Column(name = "total_con_impuestos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalConImpuestos;

    @Lob
    @Column(name = "datos_fiscales", columnDefinition = "LONGTEXT")
    private String datosFiscales; // JSON con datos fiscales del emisor y receptor

    @Column(name = "estado", length = 50)
    private String estado;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleImpuestoFactura> detallesImpuesto;

    // Getters y Setters

    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(BigDecimal totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public BigDecimal getTotalConImpuestos() {
        return totalConImpuestos;
    }

    public void setTotalConImpuestos(BigDecimal totalConImpuestos) {
        this.totalConImpuestos = totalConImpuestos;
    }

    public String getDatosFiscales() {
        return datosFiscales;
    }

    public void setDatosFiscales(String datosFiscales) {
        this.datosFiscales = datosFiscales;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleImpuestoFactura> getDetallesImpuesto() {
        return detallesImpuesto;
    }

    public void setDetallesImpuesto(List<DetalleImpuestoFactura> detallesImpuesto) {
        this.detallesImpuesto = detallesImpuesto;
    }
} 