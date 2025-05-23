package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Promocion;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PromocionRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.PromocionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @Override
    public List<Promocion> obtenerTodas() {
        return promocionRepository.findAll();
    }

    @Override
    public Optional<Promocion> obtenerPorId(Integer id) {
        return promocionRepository.findById(id);
    }

    @Override
    public Promocion guardar(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Override
    public void eliminar(Integer id) {
        promocionRepository.deleteById(id);
    }

    @Override
    public List<Promocion> obtenerPorProducto(Integer idProducto) {
        return promocionRepository.findByIdProducto(idProducto);
    }

    @Override
    public List<Promocion> obtenerPorCategoria(Integer idCategoria) {
        return promocionRepository.findByIdCategoria(idCategoria);
    }

    @Override
    public List<Promocion> obtenerPromocionesActivas() {
        return promocionRepository.findPromocionesActivas(LocalDate.now());
    }

    @Override
    public List<Promocion> obtenerPromocionesActivasPorProducto(Integer idProducto) {
        return promocionRepository.findPromocionesActivasPorProducto(idProducto, LocalDate.now());
    }

    @Override
    public Optional<Promocion> buscarPromocionAplicable(Integer idProducto, Integer idCategoria, LocalDate fechaActual) {
        List<Promocion> promociones = promocionRepository.findActivePromocionesByProductoOrCategoria(idProducto, idCategoria, fechaActual);

        if (promociones.isEmpty()) {
            return Optional.empty();
        }

        // Lógica de prioridad: Devolver la promoción más específica (producto sobre categoría)
        // o la de mayor descuento si hay varias del mismo tipo.
        // Por ahora, priorizaremos producto sobre categoría, y luego la de mayor valor si es porcentaje, o mayor valor si es monto fijo.
        // Esta lógica puede ser más compleja y configurable.

        return promociones.stream()
                .sorted(Comparator
                        // Prioridad 1: Promociones de producto sobre las de categoría
                        .comparing((Promocion p) -> p.getIdProducto() != null ? 0 : 1)
                        // Prioridad 2: Mayor valor de descuento
                        .thenComparing(Promocion::getValor, Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst();
    }

    @Override
    public BigDecimal aplicarDescuento(BigDecimal precioOriginal, Promocion promocion) {
        if (promocion == null || promocion.getValor() == null || precioOriginal == null) {
            return precioOriginal; // No hay promoción o datos inválidos, devolver precio original
        }

        String tipoPromocion = promocion.getTipoPromocion();
        BigDecimal valorPromocion = promocion.getValor();
        BigDecimal precioConDescuento = precioOriginal;

        if ("PORCENTAJE".equalsIgnoreCase(tipoPromocion)) {
            BigDecimal descuento = precioOriginal.multiply(valorPromocion.divide(new BigDecimal("100")));
            precioConDescuento = precioOriginal.subtract(descuento);
        } else if ("MONTO_FIJO".equalsIgnoreCase(tipoPromocion)) {
            precioConDescuento = precioOriginal.subtract(valorPromocion);
        }
        // Asegurarse de que el precio no sea negativo
        if (precioConDescuento.compareTo(BigDecimal.ZERO) < 0) {
            precioConDescuento = BigDecimal.ZERO;
        }

        // Redondear a 2 decimales (o la precisión que manejes para moneda)
        return precioConDescuento.setScale(2, RoundingMode.HALF_UP);
    }
}