package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "recompensas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recompensa")
    private Integer idRecompensa;

    @Column(name = "id_cliente")
    private Integer idCliente; // Campo para manejar directamente el ID del cliente

    // Asumiendo que existe una entidad Cliente y quieres mapear la relación
    // Ajusta 'referencedColumnName' si el nombre de la columna PK en la tabla 'clientes' es diferente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    @Column(name = "puntos_usados", nullable = false)
    private Integer puntosUsados;

    @Column(name = "descripcion_recompensa", length = 255, nullable = false)
    private String descripcionRecompensa;

    @Column(name = "fecha_reclamo")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp fechaReclamo;

    // Getters y Setters generados por Lombok (@Data)
}
