package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.LoteReducidoInfoDTO;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LoteService {
    List<Lote> obtenerTodos();
    Optional<Lote> obtenerPorId(Integer id);
    List<Lote> obtenerPorProducto(Integer idProducto);
    Lote guardar(Lote lote);
    void eliminar(Integer id);
    List<Lote> obtenerLotesProximosVencer(Integer diasAlerta);
    List<Lote> obtenerLotesPorRangoFechaEntrada(Date fechaInicio, Date fechaFin);
    Optional<Lote> activarLote(Integer id);
    Optional<Lote> desactivarLote(Integer id);
    List<Lote> obtenerLotesInactivos();
    Lote reducirCantidadLote(Integer idLote, Integer cantidad) throws Exception;
    List<LoteReducidoInfoDTO> reducirCantidadDeLotes(Integer idProducto, Integer cantidadTotal) throws Exception;
    Lote procesarDevolucion(Integer idLote, Integer cantidad) throws Exception;
    List<Lote> obtenerLotesActivos();

    /**
     * Crea un lote de ajuste para un producto
     * Este método se utiliza en el CASO 3 cuando se detecta una diferencia en el inventario físico
     * 
     * @param producto Producto al que se le creará el lote de ajuste
     * @param cantidad Cantidad a ajustar (positiva para aumentar, negativa para disminuir)
     * @param motivo Motivo del ajuste
     * @return El lote creado
     * @throws Exception Si ocurre un error al crear el lote
     */
    Lote crearLoteAjuste(Producto producto, Integer cantidad, String motivo) throws Exception;

    /**
     * Crea un nuevo lote para un producto
     * Este método se utiliza en el CASO 1 cuando se compra un producto
     * 
     * @param producto Producto al que se le creará el lote
     * @param cantidad Cantidad inicial del lote
     * @param numeroLote Número de lote
     * @param fechaVencimiento Fecha de vencimiento del lote
     * @param idEntrada ID del registro de movimiento de entrada
     * @return El lote creado
     */
    Lote crearNuevoLote(Producto producto, Integer cantidad, String numeroLote, Date fechaVencimiento, Integer idEntrada);
}
