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
@Table(name = "auditoria_caja")
public class AuditoriaCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    @ManyToOne
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @Column(name = "dinero_esperado", nullable = false)
    private BigDecimal dineroEsperado;

    @Column(name = "dinero_real", nullable = false)
    private BigDecimal dineroReal;

    // La columna diferencia se genera en SQL, no podríamos mapearla directamente o hacerla calculada en la entidad
    // Por ahora, no la mapearíamos directamente ya que es una columna generada en la DB
    // private BigDecimal diferencia;

    @Column(name = "fecha")
    private Timestamp fecha;

    @Column(name = "motivo")
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
} 