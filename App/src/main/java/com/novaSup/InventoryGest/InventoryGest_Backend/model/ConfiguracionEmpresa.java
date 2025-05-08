package com.novaSup.InventoryGest.InventoryGest_Backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_empresa")
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

    @Column(name = "rfc_publico_general", length = 20)
    private String rfcPublicoGeneral; // RFC para ventas a público en general

    // Getters y Setters

    public Long getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(Long idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public String getRazonSocialEmisor() {
        return razonSocialEmisor;
    }

    public void setRazonSocialEmisor(String razonSocialEmisor) {
        this.razonSocialEmisor = razonSocialEmisor;
    }

    public String getNombreComercialEmisor() {
        return nombreComercialEmisor;
    }

    public void setNombreComercialEmisor(String nombreComercialEmisor) {
        this.nombreComercialEmisor = nombreComercialEmisor;
    }

    public String getIdentificacionFiscalEmisor() {
        return identificacionFiscalEmisor;
    }

    public void setIdentificacionFiscalEmisor(String identificacionFiscalEmisor) {
        this.identificacionFiscalEmisor = identificacionFiscalEmisor;
    }

    public String getDomicilioCalle() {
        return domicilioCalle;
    }

    public void setDomicilioCalle(String domicilioCalle) {
        this.domicilioCalle = domicilioCalle;
    }

    public String getDomicilioNumeroExterior() {
        return domicilioNumeroExterior;
    }

    public void setDomicilioNumeroExterior(String domicilioNumeroExterior) {
        this.domicilioNumeroExterior = domicilioNumeroExterior;
    }

    public String getDomicilioNumeroInterior() {
        return domicilioNumeroInterior;
    }

    public void setDomicilioNumeroInterior(String domicilioNumeroInterior) {
        this.domicilioNumeroInterior = domicilioNumeroInterior;
    }

    public String getDomicilioBarrioColonia() {
        return domicilioBarrioColonia;
    }

    public void setDomicilioBarrioColonia(String domicilioBarrioColonia) {
        this.domicilioBarrioColonia = domicilioBarrioColonia;
    }

    public String getDomicilioLocalidad() {
        return domicilioLocalidad;
    }

    public void setDomicilioLocalidad(String domicilioLocalidad) {
        this.domicilioLocalidad = domicilioLocalidad;
    }

    public String getDomicilioMunicipio() {
        return domicilioMunicipio;
    }

    public void setDomicilioMunicipio(String domicilioMunicipio) {
        this.domicilioMunicipio = domicilioMunicipio;
    }

    public String getDomicilioEstadoProvincia() {
        return domicilioEstadoProvincia;
    }

    public void setDomicilioEstadoProvincia(String domicilioEstadoProvincia) {
        this.domicilioEstadoProvincia = domicilioEstadoProvincia;
    }

    public String getDomicilioPais() {
        return domicilioPais;
    }

    public void setDomicilioPais(String domicilioPais) {
        this.domicilioPais = domicilioPais;
    }

    public String getDomicilioCodigoPostal() {
        return domicilioCodigoPostal;
    }

    public void setDomicilioCodigoPostal(String domicilioCodigoPostal) {
        this.domicilioCodigoPostal = domicilioCodigoPostal;
    }

    public String getRegimenFiscalEmisor() {
        return regimenFiscalEmisor;
    }

    public void setRegimenFiscalEmisor(String regimenFiscalEmisor) {
        this.regimenFiscalEmisor = regimenFiscalEmisor;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public String getEmailFacturacion() {
        return emailFacturacion;
    }

    public void setEmailFacturacion(String emailFacturacion) {
        this.emailFacturacion = emailFacturacion;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getRfcPublicoGeneral() {
        return rfcPublicoGeneral;
    }

    public void setRfcPublicoGeneral(String rfcPublicoGeneral) {
        this.rfcPublicoGeneral = rfcPublicoGeneral;
    }
} 