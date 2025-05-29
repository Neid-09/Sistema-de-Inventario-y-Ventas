package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_empresa")
@Data // Incluye @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Column(name = "razon_social_emisor", length = 255)
    private String razonSocialEmisor;

    @Column(name = "nombre_comercial_emisor", length = 255)
    private String nombreComercialEmisor;

    @Column(name = "identificacion_fiscal_emisor", length = 20)
    private String identificacionFiscalEmisor; // RFC Emisor

    @Column(name = "domicilio_calle", length = 255)
    private String domicilioCalle;

    @Column(name = "domicilio_numero_exterior", length = 50)
    private String domicilioNumeroExterior;

    @Column(name = "domicilio_numero_interior", length = 50)
    private String domicilioNumeroInterior;

    @Column(name = "domicilio_barrio_colonia", length = 100)
    private String domicilioBarrioColonia;

    @Column(name = "domicilio_localidad", length = 100)
    private String domicilioLocalidad;

    @Column(name = "domicilio_municipio", length = 100)
    private String domicilioMunicipio;

    @Column(name = "domicilio_estado_provincia", length = 100)
    private String domicilioEstadoProvincia;

    @Column(name = "domicilio_pais", length = 100)
    private String domicilioPais;

    @Column(name = "domicilio_codigo_postal", length = 10)
    private String domicilioCodigoPostal;

    @Column(name = "regimen_fiscal_emisor", length = 100)
    private String regimenFiscalEmisor;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name = "email_contacto", length = 100)
    private String emailContacto;

    @Column(name = "pagina_web", length = 255)
    private String paginaWeb;

    @Column(name = "email_facturacion", length = 100)
    private String emailFacturacion;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "fecha_creacion", columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaActualizacion;
} 