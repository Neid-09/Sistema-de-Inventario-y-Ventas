package com.novaSup.InventoryGest.InventoryGest_Backend.repositories;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
}
