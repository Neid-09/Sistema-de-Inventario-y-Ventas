package com.novaSup.InventoryGest.InventoryGest_Backend.repositories;


import com.novaSup.InventoryGest.InventoryGest_Backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
