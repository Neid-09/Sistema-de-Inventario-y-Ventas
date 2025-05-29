package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleImpuestoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleImpuestoFacturaRepository extends JpaRepository<DetalleImpuestoFactura, Long> {
    List<DetalleImpuestoFactura> findByFacturaIdFactura(Long idFactura);
} 