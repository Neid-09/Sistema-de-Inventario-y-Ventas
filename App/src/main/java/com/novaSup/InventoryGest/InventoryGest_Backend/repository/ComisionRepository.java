package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Comision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComisionRepository extends JpaRepository<Comision, Integer> {

    // Puedes añadir métodos de consulta personalizados aquí si los necesitas.
    // Por ejemplo, para buscar comisiones por vendedor:
    List<Comision> findByVendedorIdVendedor(Integer idVendedor);

    // Para buscar comisiones por venta:
    List<Comision> findByVentaIdVenta(Integer idVenta);

    // Para buscar comisiones por estado:
    List<Comision> findByEstado(String estado);

    // Para buscar comisiones por vendedor y estado:
    List<Comision> findByVendedorIdVendedorAndEstado(Integer idVendedor, String estado);
}
