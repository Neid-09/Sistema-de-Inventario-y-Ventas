package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.AuditoriaStockRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaStockServiceImpl implements AuditoriaStockService {

    @Autowired
    private AuditoriaStockRepository auditoriaStockRepository;

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
}