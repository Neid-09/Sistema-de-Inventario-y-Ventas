package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "leida")
    private Boolean leida;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "id_referencia")
    private Integer idReferencia;
}