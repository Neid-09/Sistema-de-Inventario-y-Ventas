package com.novaSup.InventoryGest.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProveedorProductoId implements Serializable {
    private int proveedor;
    private int producto;
}
