package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "estado") // Mapea a la columna 'estado' de la BD
    private Boolean estado; // Campo para el estado (activo/inactivo)

    @Transient
    private Integer idRol;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "permisos_usuario",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<Permiso> permisosPersonalizados = new HashSet<>();

    @PostLoad
    private void setIdFromRol() {
        if (rol != null) {
            this.idRol = rol.getIdRol();
        }
    }
}