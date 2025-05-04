package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date; // Usar java.sql.Date para mapear DATE de SQL

@Entity
@Table(name = "vendedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vendedor")
    private Integer idVendedor;

    @Column(name = "id_usuario")
    private Integer idUsuario; // Campo para manejar directamente el ID del usuario

    @ManyToOne(fetch = FetchType.LAZY) // Relación con Usuario
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario", insertable = false, updatable = false) // Mapea la FK
    private Usuario usuario;

    @Column(name = "objetivo_ventas", precision = 10, scale = 2)
    private BigDecimal objetivoVentas;

    @Column(name = "fecha_contratacion")
    @Temporal(TemporalType.DATE) // Especifica que solo se mapea la fecha
    private Date fechaContratacion;

    // Getters y Setters generados por Lombok (@Data)
}
