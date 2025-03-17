package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class VentaProductoId implements Serializable {
    private int venta;
    private int producto;
}
