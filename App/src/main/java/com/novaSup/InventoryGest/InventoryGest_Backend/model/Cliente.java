package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault; // Para @ColumnDefault si se usa Hibernate

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "clientes", uniqueConstraints = {
    @UniqueConstraint(columnNames = "cedula", name = "uk_clientes_cedula"), // Constraint explícito para cédula
    @UniqueConstraint(columnNames = "correo", name = "uk_clientes_correo")   // Constraint explícito para correo
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "cedula", length = 20, nullable = false, unique = true) // unique = true aquí también es válido
    private String cedula;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "correo", length = 100, unique = true) // unique = true aquí también es válido
    private String correo;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "total_comprado", precision = 10, scale = 2)
    @ColumnDefault("0.0") // Anotación de Hibernate para el valor por defecto (alternativa a inicializar)
    private BigDecimal totalComprado = BigDecimal.ZERO; // Inicialización en Java

    @Column(name = "puntos_fidelidad")
    @ColumnDefault("0") // Anotación de Hibernate
    private Integer puntosFidelidad = 0; // Inicialización en Java

    @Column(name = "ultima_compra")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp ultimaCompra;

    // Getters y Setters generados por Lombok (@Data)

    // Relaciones inversas (opcional, si necesitas navegar desde Cliente a otras entidades)
    // @OneToMany(mappedBy = "cliente")
    // private List<Venta> ventas;

    // @OneToMany(mappedBy = "cliente")
    // private List<Recompensa> recompensasCanjeadas;
}