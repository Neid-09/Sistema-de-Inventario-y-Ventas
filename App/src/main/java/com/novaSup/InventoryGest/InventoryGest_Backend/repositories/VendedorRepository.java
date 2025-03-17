package com.novaSup.InventoryGest.InventoryGest_Backend.repositories;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {
}
