package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    @Column(name = "id_producto")
    private Integer idProducto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    private Producto producto;

    @Column(name = "stock_esperado")
    private Integer stockEsperado;

    @Column(name = "stock_real")
    private Integer stockReal;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "id_usuario")
    private Integer idUsuario;
}