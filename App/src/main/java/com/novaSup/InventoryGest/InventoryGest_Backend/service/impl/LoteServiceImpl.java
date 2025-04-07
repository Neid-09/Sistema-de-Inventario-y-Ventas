package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.LoteRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LoteServiceImpl implements LoteService {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Override
    public List<Lote> obtenerTodos() {
        return loteRepository.findByActivoTrue();
    }

    @Override
    public Optional<Lote> obtenerPorId(Integer id) {
        return loteRepository.findByIdLoteAndActivoTrue(id);
    }

    @Override
    public List<Lote> obtenerPorProducto(Integer idProducto) {
        return loteRepository.findByProductoIdProductoAndActivoTrue(idProducto);
    }

    @Override
    public Lote guardar(Lote lote) {
        if (lote.getActivo() == null) {
            lote.setActivo(true);
        }
        return loteRepository.save(lote);
    }

    @Override
    public void eliminar(Integer id) {
        // Eliminación lógica en lugar de física
        loteRepository.findById(id).ifPresent(lote -> {
            lote.setActivo(false);
            loteRepository.save(lote);
        });
    }

    @Override
    public List<Lote> obtenerLotesProximosVencer(Integer diasAlerta) {
        // Calcular la fecha límite (hoy + días de alerta)
        Calendar calendar = Calendar.getInstance();
        Date hoy = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, diasAlerta);
        Date fechaLimite = calendar.getTime();

        return loteRepository.findByFechaVencimientoBetweenAndActivoTrue(hoy, fechaLimite);
    }

    @Override
    public Optional<Lote> activarLote(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findByIdLoteAndActivoFalse(id);
        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            lote.setActivo(true);
            return Optional.of(loteRepository.save(lote));
        }
        return Optional.empty();
    }

    @Override
    public List<Lote> obtenerLotesInactivos() {
        return loteRepository.findByActivoFalse();
    }

    // Verificar lotes por vencer y notificar
    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar diariamente a medianoche
    public void verificarLotesPorVencer() {
        int diasAlerta = 30; // Configurable según necesidades
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