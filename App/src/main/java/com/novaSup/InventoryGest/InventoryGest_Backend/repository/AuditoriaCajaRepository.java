package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditoriaCajaRepository extends JpaRepository<AuditoriaCaja, Integer> {
    List<AuditoriaCaja> findByCaja(Caja caja);
    Optional<AuditoriaCaja> findFirstByUsuarioOrderByFechaDesc(Usuario usuario);
} 