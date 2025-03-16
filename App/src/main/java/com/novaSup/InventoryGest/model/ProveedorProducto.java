package com.novaSup.InventoryGest.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "proveedores_productos")
@Data
@IdClass(ProveedorProductoId.class)
public class ProveedorProducto {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
}

