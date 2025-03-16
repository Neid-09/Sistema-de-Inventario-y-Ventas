package com.novaSup.InventoryGest.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class VentaProductoId implements Serializable {
    private int venta;
    private int producto;
}
