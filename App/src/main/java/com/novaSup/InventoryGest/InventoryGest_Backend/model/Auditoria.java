package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "accion", length = 100, nullable = false)
    private String accion;

    @Column(name = "tabla_afectada", length = 100, nullable = false)
    private String tablaAfectada;

    @Column(name = "id_registro_afectado")
    private Integer idRegistroAfectado;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "datos_anteriores", columnDefinition = "LONGTEXT")
    private String datosAnteriores;

    @Column(name = "datos_nuevos", columnDefinition = "LONGTEXT")
    private String datosNuevos;
}