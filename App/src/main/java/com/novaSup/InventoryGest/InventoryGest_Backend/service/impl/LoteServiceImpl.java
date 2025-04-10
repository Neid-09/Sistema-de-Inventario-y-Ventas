package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.LoteRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LoteServiceImpl implements LoteService {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private NotificacionService notificacionService;

    @Override
    public List<Lote> obtenerTodos() {
        return loteRepository.findAll();
    }

    @Override
    public Optional<Lote> obtenerPorId(Integer id) {
        return loteRepository.findById(id);
    }

    @Override
    public List<Lote> obtenerPorProducto(Integer idProducto) {
        return loteRepository.findByProductoIdProductoAndActivoTrue(idProducto);
    }

    @Override
    @Transactional
    public Lote guardar(Lote lote) {
        Lote loteGuardado = loteRepository.save(lote);
        // Actualizar stock del producto
        if (lote.getProducto() != null) {
            stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
        } else if (lote.getIdProducto() != null) {
            stockService.actualizarStockProducto(lote.getIdProducto());
        }
        return loteGuardado;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findById(id);

        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            // Desactivar en lugar de eliminar físicamente
            lote.setActivo(false);
            loteRepository.save(lote);

            // Actualizar stock
            if (lote.getProducto() != null) {
                stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
            }
        }
    }

    @Override
    public List<Lote> obtenerLotesProximosVencer(Integer diasAlerta) {
        // Calcular fecha límite
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasAlerta);

        Date fechaActual = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaAlerta = Date.from(fechaLimite.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return loteRepository.findByFechaVencimientoBetweenAndActivoTrue(fechaActual, fechaAlerta);
    }

    @Override
    public List<Lote> obtenerLotesPorRangoFechaEntrada(Date fechaInicio, Date fechaFin) {
        return loteRepository.findByFechaEntradaBetweenAndActivoTrue(fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public Optional<Lote> activarLote(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findByIdLoteAndActivoFalse(id);

        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();

            // Verificar si el lote está vencido
            Date fechaActual = new Date();
            if (lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(fechaActual)) {
                throw new IllegalStateException("No se puede activar un lote vencido");
            }

            lote.setActivo(true);
            Lote loteActualizado = loteRepository.save(lote);

            // Actualizar stock
            if (lote.getProducto() != null) {
                stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
            }

            return Optional.of(loteActualizado);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Lote> desactivarLote(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findByIdLoteAndActivoTrue(id);

        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            lote.setActivo(false);
            Lote loteActualizado = loteRepository.save(lote);

            // Actualizar stock
            if (lote.getProducto() != null) {
                stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
            }

            return Optional.of(loteActualizado);
        }

        return Optional.empty();
    }

    @Override
    public List<Lote> obtenerLotesInactivos() {
        return loteRepository.findByActivoFalse();
    }

    @Override
    public List<Lote> obtenerLotesActivos() {
        return loteRepository.findByActivoTrue();
    }

    @Override
    @Transactional
    public Lote reducirCantidadLote(Integer idLote, Integer cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor que cero");
        }

        Optional<Lote> loteOpt = loteRepository.findById(idLote);

        if (!loteOpt.isPresent()) {
            throw new Exception("Lote no encontrado");
        }

        Lote lote = loteOpt.get();

        if (!lote.getActivo()) {
            throw new Exception("No se puede reducir cantidad de un lote inactivo");
        }

        if (lote.getCantidad() < cantidad) {
            throw new Exception("Cantidad a reducir excede el stock disponible en el lote");
        }

        // Reducir cantidad
        lote.setCantidad(lote.getCantidad() - cantidad);

        // Si la cantidad llega a cero, desactivar el lote
        if (lote.getCantidad() == 0) {
            lote.setActivo(false);
        }

        Lote loteActualizado = loteRepository.save(lote);

        // Actualizar stock del producto
        if (lote.getProducto() != null) {
            stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
        }

        return loteActualizado;
    }

    @Override
    @Transactional
    public void reducirCantidadDeLotes(Integer idProducto, Integer cantidadTotal) throws Exception {
        if (cantidadTotal <= 0) {
            throw new IllegalArgumentException("La cantidad total a reducir debe ser mayor que cero");
        }

        // Obtener lotes activos ordenados por fecha de vencimiento (FEFO)
        List<Lote> lotes = loteRepository.findByProductoIdProductoAndActivoTrueOrderByFechaVencimientoAsc(idProducto);

        if (lotes.isEmpty()) {
            throw new Exception("No hay lotes activos para el producto");
        }

        // Calcular stock total disponible
        int stockDisponible = lotes.stream().mapToInt(Lote::getCantidad).sum();

        if (stockDisponible < cantidadTotal) {
            throw new Exception("No hay suficiente stock disponible en los lotes");
        }

        int cantidadPendiente = cantidadTotal;

        // Reducir de cada lote siguiendo FEFO
        for (Lote lote : lotes) {
            if (cantidadPendiente <= 0) break;

            if (lote.getCantidad() <= cantidadPendiente) {
                // Consumir todo el lote
                cantidadPendiente -= lote.getCantidad();
                lote.setCantidad(0);
                lote.setActivo(false);
            } else {
                // Consumir parte del lote
                lote.setCantidad(lote.getCantidad() - cantidadPendiente);
                cantidadPendiente = 0;
            }

            loteRepository.save(lote);
        }

        // Actualizar stock del producto
        stockService.actualizarStockProducto(idProducto);
    }

    @Override
    @Transactional
    public Lote procesarDevolucion(Integer idLote, Integer cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a devolver debe ser mayor que cero");
        }

        Optional<Lote> loteOpt = loteRepository.findById(idLote);

        if (!loteOpt.isPresent()) {
            throw new Exception("Lote no encontrado");
        }

        Lote lote = loteOpt.get();

        // Verificar si el lote está vencido para posible alerta
        Date fechaActual = new Date();
        if (lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(fechaActual)) {
            // Se podría generar una alerta o log aquí
        }

        // Para devoluciones, reactivamos el lote si estaba inactivo
        if (!lote.getActivo()) {
            lote.setActivo(true);
        }

        // Aumentar cantidad
        lote.setCantidad(lote.getCantidad() + cantidad);

        Lote loteActualizado = loteRepository.save(lote);

        // Actualizar stock del producto
        if (lote.getProducto() != null) {
            stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
        }

        return loteActualizado;
    }

    // Verificar lotes por vencer y notificar
    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar diariamente a medianoche
    public void verificarLotesPorVencer() {
        // Días de alerta (se podría obtener de configuración)
        int diasAlerta = 30;

        List<Lote> lotesPorVencer = obtenerLotesProximosVencer(diasAlerta);

        for (Lote lote : lotesPorVencer) {
            notificacionService.notificarLoteProximoAVencer(
                    lote.getIdLote(),
                    lote.getProducto().getNombre(),
                    lote.getFechaVencimiento()
            );
        }
    }
}