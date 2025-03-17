package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProveedorProductoId implements Serializable {
    private int proveedor;
    private int producto;
}
