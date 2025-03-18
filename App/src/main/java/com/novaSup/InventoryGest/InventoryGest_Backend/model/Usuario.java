package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    private String telefono;

    @Column(nullable = false)
    private String contraseña;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Transient
    private Integer idRol;

    @PostLoad
    private void setIdFromRol() {
        if (rol != null) {
            this.idRol = rol.getIdRol();
        }
    }

}
