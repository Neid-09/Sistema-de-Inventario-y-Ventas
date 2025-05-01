package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promociones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Promocion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Integer idPromocion;

    private String descripcion;

    @Column(name = "tipo_promocion")
    private String tipoPromocion;

    private BigDecimal valor;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "id_producto")
    private Integer idProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    @JsonIgnore  // Ignora esta relación al serializar
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", insertable = false, updatable = false)
    @JsonIgnore  // Ignora esta relación al serializar
    private Categoria categoria;

    // Opcionalmente podrías agregar campos transitorios adicionales si necesitas
    // algún dato específico de las entidades relacionadas

    @Transient
    private String nombreProducto;

    @Transient
    private String nombreCategoria;

    @PostLoad
    private void loadAdditionalData() {
        if (producto != null) {
            this.nombreProducto = producto.getNombre();
        }
        if (categoria != null) {
            this.nombreCategoria = categoria.getNombre();
        }
    }
}