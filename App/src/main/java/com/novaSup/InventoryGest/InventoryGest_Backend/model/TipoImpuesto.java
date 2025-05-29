package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tipos_impuestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoImpuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_impuesto")
    private Integer idTipoImpuesto;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Lob // Para campos TEXT
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "es_porcentual")
    private Boolean esPorcentual; // tinyint(1) se mapea bien a Boolean

    @Column(name = "activo")
    private Boolean activo; // tinyint(1) se mapea bien a Boolean
} 