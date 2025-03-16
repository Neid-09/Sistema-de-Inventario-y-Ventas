package com.novaSup.InventoryGest.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "administradores")
@Data
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAdmin;
    private String nombre;
    private String correo;
    private String telefono;
    private String contraseña;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
}