package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "clientes", uniqueConstraints = {
    @UniqueConstraint(columnNames = "correo", name = "uk_clientes_correo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "documento_identidad", length = 20)
    private String documentoIdentidad;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "celular", length = 15)
    private String celular;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "requiere_factura_default", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean requiereFacturaDefault = false;

    @Column(name = "razon_social", length = 255)
    private String razonSocial;

    @Column(name = "identificacion_fiscal", length = 20)
    private String identificacionFiscal;

    @Lob
    @Column(name = "direccion_facturacion", columnDefinition = "TEXT")
    private String direccionFacturacion;

    @Column(name = "correo_facturacion", length = 100)
    private String correoFacturacion;

    @Column(name = "tipo_factura_default", length = 10)
    private String tipoFacturaDefault;

    @Column(name = "total_comprado", precision = 12, scale = 2, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal totalComprado = BigDecimal.ZERO;

    @Column(name = "puntos_fidelidad", columnDefinition = "INT(11) DEFAULT 0")
    private Integer puntosFidelidad = 0;

    @Column(name = "ultima_compra")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp ultimaCompra;

    @Column(name = "limite_credito", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp fechaActualizacion;

    @Column(name = "activo", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean activo = true;

    // Getters y Setters generados por Lombok (@Data)

    // Relaciones inversas (opcional, si necesitas navegar desde Cliente a otras entidades)
    // @OneToMany(mappedBy = "cliente")
    // private List<Venta> ventas;

    // @OneToMany(mappedBy = "cliente")
    // private List<Recompensa> recompensasCanjeadas;
}