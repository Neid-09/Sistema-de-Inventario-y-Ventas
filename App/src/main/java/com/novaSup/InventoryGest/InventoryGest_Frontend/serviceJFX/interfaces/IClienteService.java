package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de Clientes en JavaFX.
 * Define las operaciones CRUD y otras específicas para clientes.
 */
public interface IClienteService {

    /**
     * Obtiene todos los clientes registrados.
     * @return Lista de ClienteFX.
     * @throws Exception Si ocurre un error durante la comunicación con la API.
     */
    List<ClienteFX> obtenerTodosLosClientes() throws Exception;

    /**
     * Obtiene un cliente específico por su ID.
     * @param id El ID del cliente a buscar.
     * @return Un Optional que contiene el ClienteFX si se encuentra, o vacío si no.
     * @throws Exception Si ocurre un error durante la comunicación con la API.
     */
    Optional<ClienteFX> obtenerClientePorId(Integer id) throws Exception;

    /**
     * Obtiene un cliente específico por su número de cédula.
     * @param cedula La cédula del cliente a buscar.
     * @return Un Optional que contiene el ClienteFX si se encuentra, o vacío si no.
     * @throws Exception Si ocurre un error durante la comunicación con la API.
     */
    Optional<ClienteFX> obtenerClientePorCedula(String cedula) throws Exception;

    /**
     * Guarda un nuevo cliente o actualiza uno existente si el ID ya existe.
     * Nota: La API backend distingue entre crear (POST) y actualizar (PUT) basado en la presencia/ausencia de ID o el endpoint.
     * Esta implementación unificará la lógica si es posible, o se crearán métodos separados si es necesario.
     * @param cliente El ClienteFX a guardar/actualizar.
     * @return El ClienteFX guardado o actualizado.
     * @throws Exception Si ocurre un error durante la comunicación con la API o validación.
     */
    ClienteFX guardarOActualizarCliente(ClienteFX cliente) throws Exception;


    /**
     * Elimina un cliente por su ID.
     * @param id El ID del cliente a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws Exception Si ocurre un error durante la comunicación con la API.
     */
    boolean eliminarCliente(Integer id) throws Exception;

    /**
     * Añade puntos de fidelidad a un cliente específico.
     * @param idCliente El ID del cliente.
     * @param puntos La cantidad de puntos a añadir (debe ser positiva).
     * @return El ClienteFX actualizado con los nuevos puntos.
     * @throws Exception Si ocurre un error, el cliente no existe o los puntos son inválidos.
     */
    ClienteFX anadirPuntosFidelidad(Integer idCliente, int puntos) throws Exception;
}
