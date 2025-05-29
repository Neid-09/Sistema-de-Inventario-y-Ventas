package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CajaReporteConsolidadoDTO {
    private Integer idCaja;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre; // Puede ser null si la caja está abierta
    private BigDecimal dineroInicial;
    private BigDecimal totalMovimientosEfectivoManuales; // Entradas/Salidas manuales en efectivo
    private BigDecimal totalVentasEfectivo;
    private BigDecimal totalVentasTarjeta;
    private BigDecimal totalVentasOtros;
    private BigDecimal totalGeneralVentas; // Suma de totalVentasEfectivo, totalVentasTarjeta, totalVentasOtros
    private BigDecimal totalEsperadoCaja; // dineroInicial + totalMovimientosEfectivoManuales + totalVentasEfectivo
    private Integer totalUnidadesVendidas; // Total de unidades de productos vendidos

    // Getters y Setters

    public Integer getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getDineroInicial() {
        return dineroInicial;
    }

    public void setDineroInicial(BigDecimal dineroInicial) {
        this.dineroInicial = dineroInicial;
    }

    public BigDecimal getTotalMovimientosEfectivoManuales() {
        return totalMovimientosEfectivoManuales;
    }

    public void setTotalMovimientosEfectivoManuales(BigDecimal totalMovimientosEfectivoManuales) {
        this.totalMovimientosEfectivoManuales = totalMovimientosEfectivoManuales;
    }

    public BigDecimal getTotalVentasEfectivo() {
        return totalVentasEfectivo;
    }

    public void setTotalVentasEfectivo(BigDecimal totalVentasEfectivo) {
        this.totalVentasEfectivo = totalVentasEfectivo;
    }

    public BigDecimal getTotalVentasTarjeta() {
        return totalVentasTarjeta;
    }

    public void setTotalVentasTarjeta(BigDecimal totalVentasTarjeta) {
        this.totalVentasTarjeta = totalVentasTarjeta;
    }

    public BigDecimal getTotalVentasOtros() {
        return totalVentasOtros;
    }

    public void setTotalVentasOtros(BigDecimal totalVentasOtros) {
        this.totalVentasOtros = totalVentasOtros;
    }

    public BigDecimal getTotalGeneralVentas() {
        return totalGeneralVentas;
    }

    public void setTotalGeneralVentas(BigDecimal totalGeneralVentas) {
        this.totalGeneralVentas = totalGeneralVentas;
    }

    public BigDecimal getTotalEsperadoCaja() {
        return totalEsperadoCaja;
    } // Este cálculo se hará en el servicio

    public void setTotalEsperadoCaja(BigDecimal totalEsperadoCaja) {
        this.totalEsperadoCaja = totalEsperadoCaja;
    }

    public Integer getTotalUnidadesVendidas() {
        return totalUnidadesVendidas;
    }

    public void setTotalUnidadesVendidas(Integer totalUnidadesVendidas) {
        this.totalUnidadesVendidas = totalUnidadesVendidas;
    }
} 