package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tasas_impuestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TasaImpuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tasa")
    private Integer idTasa;
    
    @Transient // Campo para almacenar el ID del tipo de impuesto
    private Integer tipoImpuestoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_impuesto", nullable = false)
    private TipoImpuesto tipoImpuesto;

    @Column(name = "tasa", precision = 5, scale = 2)
    private BigDecimal tasa;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_inicio")
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_fin")
    private Date fechaFin;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @PostLoad
    private void onLoad() {
        if (this.tipoImpuesto != null) {
            // Acceder al ID de un proxy generalmente es seguro y no debería
            // desencadenar la inicialización completa del proxy.
            this.tipoImpuestoId = this.tipoImpuesto.getIdTipoImpuesto();
        }
    }
} 