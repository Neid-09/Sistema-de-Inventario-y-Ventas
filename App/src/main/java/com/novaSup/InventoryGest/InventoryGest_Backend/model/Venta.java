package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "venta") // Especifica el nombre de la tabla
@Data // Lombok: Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Genera constructor sin argumentos
@AllArgsConstructor // Lombok: Genera constructor con todos los argumentos
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta") // Mapea al nombre de columna correcto
    private Integer idVenta; // Cambiado a Integer para coincidir con int(11)

    @Column(name = "fecha") // Mapea al nombre de columna correcto
    private Timestamp fecha;

    @Column(name = "id_cliente") // Mapea al nombre de columna correcto
    private Integer idCliente; // Asumiendo que existe una entidad Cliente o se maneja solo el ID

    // @ManyToOne // Descomentar si existe entidad Cliente y se quiere mapear la relación
    // @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    // private Cliente cliente;

    @Column(name = "id_vendedor") // Mapea al nombre de columna correcto
    private Integer idVendedor;

    @ManyToOne(fetch = FetchType.LAZY) // Relación con Vendedor
    @JoinColumn(name = "id_vendedor", referencedColumnName = "id_vendedor", insertable = false, updatable = false) // Mapea la FK
    private Vendedor vendedor;

    @Column(name = "total", precision = 10, scale = 2) // Mapea al nombre y tipo de columna correcto
    private BigDecimal total;

    @Column(name = "tipo_pago", length = 50, nullable = false)
    private String tipoPago;

    @Column(name = "requiere_factura") // Mapea al nombre de columna correcto
    private Boolean requiereFactura; // tinyint(1) mapea a Boolean

    @Column(name = "numero_venta", length = 50) // Mapea al nombre y tipo de columna correcto
    private String numeroVenta;

    @Column(name = "aplicar_impuestos") // Mapea al nombre de columna correcto
    private Boolean aplicarImpuestos; // tinyint(1) mapea a Boolean

    // Comentado o eliminado si no se usa DetallesVenta por ahora
    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "venta")
    // private List<DetalleVenta> detalles;

    // Los Getters y Setters son generados por Lombok (@Data)
}