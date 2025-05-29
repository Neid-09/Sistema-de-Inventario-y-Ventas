package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "impuestos_aplicables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpuestoAplicable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_impuesto_aplicable")
    private Integer idImpuestoAplicable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tasa", nullable = false)
    private TasaImpuesto tasaImpuesto;

    @Transient
    private Integer idTasa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = true) // Puede ser nullable si el impuesto aplica a categoría
    private Producto producto;

    @Transient
    private Integer idProducto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = true) // Puede ser nullable si el impuesto aplica a producto
    private Categoria categoria;

    @Transient
    private Integer idCategoria;

    @Column(name = "aplica")
    private Boolean aplica;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_inicio")
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_fin")
    private Date fechaFin;

    @PostLoad
    private void loadTransientIds() {
        if (this.tasaImpuesto != null) {
            this.idTasa = this.tasaImpuesto.getIdTasa();
        }
        if (this.producto != null) {
            this.idProducto = this.producto.getIdProducto();
        }
        if (this.categoria != null) {
            this.idCategoria = this.categoria.getIdCategoria();
        }
    }
} 