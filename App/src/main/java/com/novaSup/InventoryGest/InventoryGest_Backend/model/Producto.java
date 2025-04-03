package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(length = 50)
    private String codigo;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_costo", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioCosto;

    @Column(name = "precio_venta", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioVenta;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Transient
    private Integer idCategoria;

    @Column(nullable = false)
    private Boolean estado = true;

    @PostLoad
    private void setIdFromCategoria() {
        if (categoria != null) {
            this.idCategoria = categoria.getIdCategoria();
        }
    }
}