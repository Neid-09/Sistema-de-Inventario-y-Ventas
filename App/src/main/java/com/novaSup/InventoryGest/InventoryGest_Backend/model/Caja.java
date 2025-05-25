package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "caja")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Integer idCaja;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_apertura")
    private Timestamp fechaApertura;

    @Column(name = "fecha_cierre")
    private Timestamp fechaCierre;

    @Column(name = "dinero_inicial", nullable = false)
    private BigDecimal dineroInicial;

    @Column(name = "dinero_total")
    private BigDecimal dineroTotal;

    @Column(name = "estado")
    private String estado;
} 