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

    @ManyToOne(fetch = FetchType.LAZY) 
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
} 