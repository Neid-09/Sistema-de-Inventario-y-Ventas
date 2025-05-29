package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CajaReporteConsolidadoFX {
    private final IntegerProperty idCaja;
    private final ObjectProperty<LocalDateTime> fechaApertura;
    private final ObjectProperty<LocalDateTime> fechaCierre;
    private final ObjectProperty<BigDecimal> dineroInicial;
    private final ObjectProperty<BigDecimal> totalEntradas;
    private final ObjectProperty<BigDecimal> totalSalidas;
    private final ObjectProperty<BigDecimal> totalMovimientosEfectivoManuales;
    private final ObjectProperty<BigDecimal> totalVentasEfectivo;
    private final ObjectProperty<BigDecimal> totalVentasTarjeta;
    private final ObjectProperty<BigDecimal> totalVentasOtros;
    private final ObjectProperty<BigDecimal> totalGeneralVentas;
    private final ObjectProperty<BigDecimal> totalEsperadoCaja;
    private final IntegerProperty totalUnidadesVendidas;

    public CajaReporteConsolidadoFX() {
        this.idCaja = new SimpleIntegerProperty(this, "idCaja");
        this.fechaApertura = new SimpleObjectProperty<>(this, "fechaApertura");
        this.fechaCierre = new SimpleObjectProperty<>(this, "fechaCierre");
        this.dineroInicial = new SimpleObjectProperty<>(this, "dineroInicial");
        this.totalEntradas = new SimpleObjectProperty<>(this, "totalEntradas");
        this.totalSalidas = new SimpleObjectProperty<>(this, "totalSalidas");
        this.totalMovimientosEfectivoManuales = new SimpleObjectProperty<>(this, "totalMovimientosEfectivoManuales");
        this.totalVentasEfectivo = new SimpleObjectProperty<>(this, "totalVentasEfectivo");
        this.totalVentasTarjeta = new SimpleObjectProperty<>(this, "totalVentasTarjeta");
        this.totalVentasOtros = new SimpleObjectProperty<>(this, "totalVentasOtros");
        this.totalGeneralVentas = new SimpleObjectProperty<>(this, "totalGeneralVentas");
        this.totalEsperadoCaja = new SimpleObjectProperty<>(this, "totalEsperadoCaja");
        this.totalUnidadesVendidas = new SimpleIntegerProperty(this, "totalUnidadesVendidas");
    }

    public IntegerProperty idCajaProperty() {
        return idCaja;
    }

    public ObjectProperty<LocalDateTime> fechaAperturaProperty() {
        return fechaApertura;
    }

    public ObjectProperty<LocalDateTime> fechaCierreProperty() {
        return fechaCierre;
    }

    public ObjectProperty<BigDecimal> dineroInicialProperty() {
        return dineroInicial;
    }

    public ObjectProperty<BigDecimal> totalEntradasProperty() {
        return totalEntradas;
    }

    public ObjectProperty<BigDecimal> totalSalidasProperty() {
        return totalSalidas;
    }

    public ObjectProperty<BigDecimal> totalMovimientosEfectivoManualesProperty() {
        return totalMovimientosEfectivoManuales;
    }

    public ObjectProperty<BigDecimal> totalVentasEfectivoProperty() {
        return totalVentasEfectivo;
    }

    public ObjectProperty<BigDecimal> totalVentasTarjetaProperty() {
        return totalVentasTarjeta;
    }

    public ObjectProperty<BigDecimal> totalVentasOtrosProperty() {
        return totalVentasOtros;
    }

    public ObjectProperty<BigDecimal> totalGeneralVentasProperty() {
        return totalGeneralVentas;
    }

    public ObjectProperty<BigDecimal> totalEsperadoCajaProperty() {
        return totalEsperadoCaja;
    }

    public IntegerProperty totalUnidadesVendidasProperty() {
        return totalUnidadesVendidas;
    }

    public Integer getIdCaja() {
        return idCaja.get();
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura.get();
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre.get();
    }

    public BigDecimal getDineroInicial() {
        return dineroInicial.get();
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas.get();
    }

    public BigDecimal getTotalSalidas() {
        return totalSalidas.get();
    }

    public BigDecimal getTotalMovimientosEfectivoManuales() {
        return totalMovimientosEfectivoManuales.get();
    }

    public BigDecimal getTotalVentasEfectivo() {
        return totalVentasEfectivo.get();
    }

    public BigDecimal getTotalVentasTarjeta() {
        return totalVentasTarjeta.get();
    }

    public BigDecimal getTotalVentasOtros() {
        return totalVentasOtros.get();
    }

    public BigDecimal getTotalGeneralVentas() {
        return totalGeneralVentas.get();
    }

    public BigDecimal getTotalEsperadoCaja() {
        return totalEsperadoCaja.get();
    }

    public int getTotalUnidadesVendidas() {
        return totalUnidadesVendidas.get();
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja.set(idCaja);
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura.set(fechaApertura);
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre.set(fechaCierre);
    }

    public void setDineroInicial(BigDecimal dineroInicial) {
        this.dineroInicial.set(dineroInicial);
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas.set(totalEntradas);
    }

    public void setTotalSalidas(BigDecimal totalSalidas) {
        this.totalSalidas.set(totalSalidas);
    }

    public void setTotalMovimientosEfectivoManuales(BigDecimal totalMovimientosEfectivoManuales) {
        this.totalMovimientosEfectivoManuales.set(totalMovimientosEfectivoManuales);
    }

    public void setTotalVentasEfectivo(BigDecimal totalVentasEfectivo) {
        this.totalVentasEfectivo.set(totalVentasEfectivo);
    }

    public void setTotalVentasTarjeta(BigDecimal totalVentasTarjeta) {
        this.totalVentasTarjeta.set(totalVentasTarjeta);
    }

    public void setTotalVentasOtros(BigDecimal totalVentasOtros) {
        this.totalVentasOtros.set(totalVentasOtros);
    }

    public void setTotalGeneralVentas(BigDecimal totalGeneralVentas) {
        this.totalGeneralVentas.set(totalGeneralVentas);
    }

    public void setTotalEsperadoCaja(BigDecimal totalEsperadoCaja) {
        this.totalEsperadoCaja.set(totalEsperadoCaja);
    }

    public void setTotalUnidadesVendidas(int totalUnidadesVendidas) {
        this.totalUnidadesVendidas.set(totalUnidadesVendidas);
    }
} 