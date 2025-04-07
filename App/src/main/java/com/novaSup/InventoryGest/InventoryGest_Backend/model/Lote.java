package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "lotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "id_entrada")
    private Integer idEntrada;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Transient
    private Integer idProducto;

    @Column(name = "numero_lote", length = 50)
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    private Integer cantidad;

    @Column(nullable = false)
    private Boolean activo = true;

    @PostLoad
    private void loadIds() {
        if (producto != null) {
            this.idProducto = producto.getIdProducto();
        }
    }
}