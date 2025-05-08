package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "impuestos_aplicables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpuestoAplicable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_impuesto_aplicable")
    private Integer idImpuestoAplicable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tasa", nullable = false)
    private TasaImpuesto tasaImpuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = true) // Puede ser nullable si el impuesto aplica a categoría
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = true) // Puede ser nullable si el impuesto aplica a producto
    private Categoria categoria;

    @Column(name = "aplica")
    private Boolean aplica;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_inicio")
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_fin")
    private Date fechaFin;
} 