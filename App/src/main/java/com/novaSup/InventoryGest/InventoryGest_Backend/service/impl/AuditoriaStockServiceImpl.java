package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.AuditoriaStockRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaStockService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaStockServiceImpl implements AuditoriaStockService {

    private final AuditoriaStockRepository auditoriaStockRepository;
    private final LoteService loteService;
    private final StockServiceImpl stockService;

    public AuditoriaStockServiceImpl(
            AuditoriaStockRepository auditoriaStockRepository,
            LoteService loteService,
            StockServiceImpl stockService) {
        this.auditoriaStockRepository = auditoriaStockRepository;
        this.loteService = loteService;
        this.stockService = stockService;
    }

    @Override
    public List<AuditoriaStock> obtenerTodas() {
        return auditoriaStockRepository.findAll();
    }

    @Override
    public Optional<AuditoriaStock> obtenerPorId(Integer id) {
        return auditoriaStockRepository.findById(id);
    }

    @Override
    public List<AuditoriaStock> obtenerPorProducto(Integer idProducto) {
        return auditoriaStockRepository.findByIdProducto(idProducto);
    }

    @Override
    public List<AuditoriaStock> obtenerPorRangoFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return auditoriaStockRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public AuditoriaStock registrarAuditoria(Integer idProducto, Integer stockEsperado,
                                             Integer stockReal, String motivo, Integer idUsuario) {
        AuditoriaStock auditoria = new AuditoriaStock();
        auditoria.setIdProducto(idProducto);
        auditoria.setStockEsperado(stockEsperado);
        auditoria.setStockReal(stockReal);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setMotivo(motivo);

        // Si no se proporciona el ID de usuario, intentar obtenerlo del contexto de seguridad
        if (idUsuario == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                // Aquí deberías extraer el ID del usuario según tu implementación
                // Este es un ejemplo genérico que dependerá de tu sistema de autenticación
            }
        } else {
            auditoria.setIdUsuario(idUsuario);
        }

        return auditoriaStockRepository.save(auditoria);
    }

    @Override
    public AuditoriaStock guardar(AuditoriaStock auditoriaStock) {
        if (auditoriaStock.getFecha() == null) {
            auditoriaStock.setFecha(LocalDateTime.now());
        }
        return auditoriaStockRepository.save(auditoriaStock);
    }

    @Override
    @Transactional
    public AuditoriaStock registrarDiferenciaInventario(
            Producto producto,
            Integer stockReal,
            String motivo,
            Integer idUsuario) throws Exception {

        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }

        if (stockReal < 0) {
            throw new IllegalArgumentException("El stock real no puede ser negativo");
        }

        // Obtener el stock actual del sistema
        int stockSistema = stockService.calcularStockProducto(producto.getIdProducto());

        // Si no hay diferencia, no hacer nada
        if (stockSistema == stockReal) {
            throw new IllegalArgumentException("No hay diferencia entre el stock del sistema y el stock real");
        }

        // Calcular la diferencia
        int diferencia = stockReal - stockSistema;

        // Crear registro de auditoría
        AuditoriaStock auditoria = new AuditoriaStock();
        auditoria.setIdProducto(producto.getIdProducto());
        auditoria.setProducto(producto);
        auditoria.setStockEsperado(stockSistema);
        auditoria.setStockReal(stockReal);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setMotivo(motivo);
        auditoria.setIdUsuario(idUsuario);

        // Guardar la auditoría
        AuditoriaStock auditoriaGuardada = auditoriaStockRepository.save(auditoria);

        // Ajustar el stock creando o modificando lotes
        loteService.crearLoteAjuste(producto, diferencia, "Ajuste por auditoría: " + motivo);

        // Actualizar el stock del producto
        stockService.actualizarStockProducto(producto.getIdProducto());

        return auditoriaGuardada;
    }
}
