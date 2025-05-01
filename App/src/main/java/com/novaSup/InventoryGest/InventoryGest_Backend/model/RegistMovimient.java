package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa el registro de movimientos de productos en el inventario.
 * A pesar del nombre de la tabla en la base de datos (entradas_productos), esta entidad
 * registra tanto entradas (compras, devoluciones, ajustes positivos) como salidas
 * (ventas, ajustes negativos) de productos en el inventario.
 */
@Entity
@Table(name = "entradas_productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistMovimient {
    /**
     * Identificador único del registro de movimiento
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrada")
    private Integer idEntrada;

    /**
     * Producto asociado al movimiento
     */
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    /**
     * Proveedor asociado al movimiento (solo aplica para movimientos de tipo ENTRADA)
     */
    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    /**
     * Cantidad de unidades en el movimiento
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Fecha y hora del movimiento
     */
    @Column
    private LocalDateTime fecha = LocalDateTime.now();

    /**
     * Tipo de movimiento: ENTRADA o SALIDA
     */
    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento;

    /**
     * Precio unitario del producto en el momento del movimiento
     */
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    /**
     * Motivo del movimiento (opcional)
     * Ejemplos: "Compra", "Venta", "Ajuste de inventario", "Devolución", etc.
     */
    @Column(name = "motivo")
    private String motivo;
}