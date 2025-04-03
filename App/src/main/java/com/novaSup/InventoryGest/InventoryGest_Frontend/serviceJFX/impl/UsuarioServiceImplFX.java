package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de usuarios para la interfaz gráfica.
 */
public class UsuarioServiceImplFX implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImplFX.class);
    private static final String BASE_URL = "http://localhost:8080";
    private static final String HEADER_USUARIO_ID = "usuario-id";
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Obtiene la lista de roles disponibles en el sistema.
     */
    @Override
    public List<RolFX> obtenerRoles() throws Exception {
        try {
            // Ya no necesitamos enviar el encabezado usuario-id
            String response = HttpClient.get(BASE_URL + "/roles");

            JsonNode rolesNode = mapper.readTree(response);
            List<RolFX> roles = new ArrayList<>();

            for (JsonNode rolNode : rolesNode) {
                if (rolNode == null) continue;

                Integer idRol = rolNode.has("idRol") ? rolNode.get("idRol").asInt() : null;
                String nombre = rolNode.has("nombre") ? rolNode.get("nombre").asText() : "";

                roles.add(new RolFX(idRol, nombre));
            }

            return roles;
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Sin mensaje de error";
            logger.error("Error al obtener roles: {}", mensaje);
            throw new Exception("Error al obtener roles: " + mensaje);
        }
    }



    /**
     * Obtiene la lista de todos los usuarios registrados.
     */
    @Override
    public List<UsuarioFX> obtenerUsuarios() throws Exception {
        try {
            // Ya no necesitamos enviar el encabezado usuario-id
            String response = HttpClient.get(BASE_URL + "/usuarios");

            JsonNode usuariosNode = mapper.readTree(response);
            List<UsuarioFX> usuarios = new ArrayList<>();

            for (JsonNode usuarioNode : usuariosNode) {
                try {
                    UsuarioFX usuario = convertirDeJSON(usuarioNode);
                    usuarios.add(usuario);
                } catch (Exception e) {
                    logger.error("Error procesando usuario: {}", e.getMessage());
                }
            }
            return usuarios;
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Sin mensaje de error";
            logger.error("Error al obtener usuarios: {}", mensaje);
            throw new Exception("Error al obtener usuarios: " + mensaje);
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    @Override
    public UsuarioFX registrarUsuario(UsuarioFX usuario) throws Exception {
        try {
            // Convertir objeto a JSON
            ObjectNode usuarioDTO = mapper.createObjectNode();
            usuarioDTO.put("nombre", usuario.getNombre());
            usuarioDTO.put("correo", usuario.getCorreo());
            usuarioDTO.put("telefono", usuario.getTelefono());
            usuarioDTO.put("contraseña", usuario.getContraseña());

            // Agregar ID del rol si existe
            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                usuarioDTO.put("idRol", usuario.getRol().getIdRol());
            }

            // Realizar petición HTTP (HttpClient añadirá automáticamente el token JWT)
            String response = HttpClient.post(
                    BASE_URL + "/usuarios",
                    mapper.writeValueAsString(usuarioDTO)
            );

            // Procesar respuesta
            JsonNode usuarioNode = mapper.readTree(response);
            return convertirDeJSON(usuarioNode);
        } catch (Exception e) {
            throw new Exception("Error al registrar usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    @Override
    public UsuarioFX actualizarUsuario(Integer id, UsuarioFX usuario) throws Exception {
        try {
            // Convertir objeto a JSON
            ObjectNode usuarioDTO = mapper.createObjectNode();
            usuarioDTO.put("nombre", usuario.getNombre());
            usuarioDTO.put("correo", usuario.getCorreo());
            usuarioDTO.put("telefono", usuario.getTelefono());

            // Solo incluir contraseña si no está vacía
            if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
                usuarioDTO.put("contraseña", usuario.getContraseña());
            }

            // Agregar ID del rol si existe
            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                usuarioDTO.put("idRol", usuario.getRol().getIdRol());
            }

            // Realizar petición HTTP (ahora sin encabezado usuario-id)
            String response = HttpClient.put(
                    BASE_URL + "/usuarios/" + id,
                    mapper.writeValueAsString(usuarioDTO)
            );

            // Procesar respuesta
            JsonNode usuarioNode = mapper.readTree(response);
            return convertirDeJSON(usuarioNode);
        } catch (Exception e) {
            throw new Exception("Error al actualizar usuario: " + e.getMessage());
        }
    }

    /**
     * Elimina un usuario del sistema.
     */
    @Override
    public void eliminarUsuario(Integer id) throws Exception {
        try {
            // El HttpClient ya enviará el token automáticamente
            HttpClient.delete(BASE_URL + "/usuarios/" + id);
        } catch (Exception e) {
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para convertir JSON a objeto UsuarioFX
     */
    private UsuarioFX convertirDeJSON(JsonNode usuarioNode) {
        Integer idUsuario = usuarioNode.has("idUsuario") ? usuarioNode.get("idUsuario").asInt() : null;
        String nombre = usuarioNode.has("nombre") ? usuarioNode.get("nombre").asText() : "";
        String correo = usuarioNode.has("correo") ? usuarioNode.get("correo").asText() : "";
        String telefono = usuarioNode.has("telefono") ? usuarioNode.get("telefono").asText() : "";
        String contraseña = usuarioNode.has("contraseña") ? usuarioNode.get("contraseña").asText() : "";

        // Procesar datos del rol
        RolFX rol = null;
        if (usuarioNode.has("rol") && !usuarioNode.get("rol").isNull()) {
            JsonNode rolNode = usuarioNode.get("rol");
            Integer idRol = rolNode.has("idRol") ? rolNode.get("idRol").asInt() : null;
            String nombreRol = rolNode.has("nombre") ? rolNode.get("nombre").asText() : "";
            rol = new RolFX(idRol, nombreRol);
        }

        return new UsuarioFX(idUsuario, nombre, correo, telefono, contraseña, rol);
    }
}