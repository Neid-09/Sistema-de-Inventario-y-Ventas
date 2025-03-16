package com.novaSup.InventoryGest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ventas_productos")
@Data
@IdClass(VentaProductoId.class)
public class VentaProducto {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private int cantidad;
    private BigDecimal precioUnitario;
}

