package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

public class CajaReporteConsolidadoFX {
    private final ObjectProperty<CajaResponseFX> caja;
    private final ObjectProperty<BigDecimal> totalEntradas;
    private final ObjectProperty<BigDecimal> totalSalidas;
    private final ObjectProperty<BigDecimal> saldoCalculado;

    public CajaReporteConsolidadoFX() {
        this.caja = new SimpleObjectProperty<>(this, "caja");
        this.totalEntradas = new SimpleObjectProperty<>(this, "totalEntradas");
        this.totalSalidas = new SimpleObjectProperty<>(this, "totalSalidas");
        this.saldoCalculado = new SimpleObjectProperty<>(this, "saldoCalculado");
    }

    // Getters para Properties
    public ObjectProperty<CajaResponseFX> cajaProperty() {
        return caja;
    }

    public ObjectProperty<BigDecimal> totalEntradasProperty() {
        return totalEntradas;
    }

    public ObjectProperty<BigDecimal> totalSalidasProperty() {
        return totalSalidas;
    }

    public ObjectProperty<BigDecimal> saldoCalculadoProperty() {
        return saldoCalculado;
    }

    // Getters
    public CajaResponseFX getCaja() {
        return caja.get();
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas.get();
    }

    public BigDecimal getTotalSalidas() {
        return totalSalidas.get();
    }

    public BigDecimal getSaldoCalculado() {
        return saldoCalculado.get();
    }

    // Setters
    public void setCaja(CajaResponseFX caja) {
        this.caja.set(caja);
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas.set(totalEntradas);
    }

    public void setTotalSalidas(BigDecimal totalSalidas) {
        this.totalSalidas.set(totalSalidas);
    }

    public void setSaldoCalculado(BigDecimal saldoCalculado) {
        this.saldoCalculado.set(saldoCalculado);
    }
} 