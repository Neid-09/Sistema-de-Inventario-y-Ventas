package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ResumenInventarioDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoStockCriticoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoSobrestockDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaz para el servicio de reportes.
 * Define los métodos para obtener los reportes de inventario de forma asíncrona.
 */
public interface IReporteService {
    
    /**
     * Obtiene el resumen general del inventario de forma asíncrona.
     * @return CompletableFuture con ResumenInventarioDTO con las estadísticas del inventario
     */
    CompletableFuture<ResumenInventarioDTO> obtenerResumenInventario();
    
    /**
     * Obtiene la lista de productos con stock crítico (bajo el mínimo) de forma asíncrona.
     * @return CompletableFuture con Lista de ProductoStockCriticoDTO
     */
    CompletableFuture<List<ProductoStockCriticoDTO>> obtenerProductosStockCritico();
    
    /**
     * Obtiene la lista de productos con sobrestock (sobre el máximo) de forma asíncrona.
     * @return CompletableFuture con Lista de ProductoSobrestockDTO
     */
    CompletableFuture<List<ProductoSobrestockDTO>> obtenerProductosSobrestock();
}
