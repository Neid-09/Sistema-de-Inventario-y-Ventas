package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ConfiguracionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {
    // Podríamos necesitar un método para encontrar la configuración activa si hubiera varias
    // Por ahora, asumimos que solo hay una fila o se recupera la primera.
    Optional<ConfiguracionEmpresa> findFirstByOrderByIdConfiguracionAsc();
} 