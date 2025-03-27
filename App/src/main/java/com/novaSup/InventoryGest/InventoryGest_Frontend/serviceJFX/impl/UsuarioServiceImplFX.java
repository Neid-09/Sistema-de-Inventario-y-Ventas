package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Proporciona métodos para la gestión de usuarios y roles mediante
 * comunicación con la API REST del backend.
 */
public class UsuarioServiceImplFX implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImplFX.class);
    private static final String BASE_URL = "http://localhost:8080";
    private static final String HEADER_USUARIO_ID = "usuario-id";
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Obtiene la lista de roles disponibles en el sistema.
     *
     * @return Lista de objetos RolFX
     * @throws Exception Si ocurre un error durante la comunicación con el backend
     */
    @Override
    public List<RolFX> obtenerRoles() throws Exception {
        String jsonResponse = HttpClient.get(
                BASE_URL + "/roles",
                HEADER_USUARIO_ID,
                getUsuarioIdAutenticado().toString()
        );

        List<RolFX> roles = new ArrayList<>();
        JsonNode rolesArray = mapper.readTree(jsonResponse);

        for (JsonNode roleNode : rolesArray) {
            RolFX rol = new RolFX(
                    roleNode.get("idRol").asInt(),
                    roleNode.get("rol").asText()
            );
            roles.add(rol);
        }

        return roles;
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return Lista de objetos UsuarioFX
     * @throws Exception Si ocurre un error durante la comunicación con el backend
     */
    @Override
    public List<UsuarioFX> obtenerUsuarios() throws Exception {
        String jsonResponse = HttpClient.get(
                BASE_URL + "/usuarios/listar",
                HEADER_USUARIO_ID,
                getUsuarioIdAutenticado().toString()
        );

        List<UsuarioFX> usuarios = new ArrayList<>();
        JsonNode usuariosArray = mapper.readTree(jsonResponse);

        for (JsonNode usuarioNode : usuariosArray) {
            usuarios.add(convertirJsonAUsuario(usuarioNode));
        }

        return usuarios;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuario Objeto UsuarioFX con los datos del usuario a registrar
     * @return El objeto UsuarioFX con los datos del usuario registrado
     * @throws Exception Si ocurre un error durante la comunicación con el backend
     */
    @Override
    public UsuarioFX registrarUsuario(UsuarioFX usuario) throws Exception {
        String jsonBody = crearJsonUsuario(usuario);
        String jsonResponse = HttpClient.post(
                BASE_URL + "/usuarios/registrar",
                jsonBody,
                HEADER_USUARIO_ID,
                getUsuarioIdAutenticado().toString()
        );

        return procesarRespuestaUsuario(jsonResponse);
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id ID del usuario a actualizar
     * @param usuario Objeto UsuarioFX con los nuevos datos del usuario
     * @return El objeto UsuarioFX actualizado
     * @throws Exception Si ocurre un error durante la comunicación con el backend
     */
    @Override
    public UsuarioFX actualizarUsuario(Integer id, UsuarioFX usuario) throws Exception {
        String jsonBody = crearJsonUsuario(usuario);
        String jsonResponse = HttpClient.put(
                BASE_URL + "/usuarios/actualizar/" + id,
                jsonBody,
                HEADER_USUARIO_ID,
                getUsuarioIdAutenticado().toString()
        );

        return procesarRespuestaUsuario(jsonResponse);
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param id ID del usuario a eliminar
     * @throws Exception Si ocurre un error durante la comunicación con el backend
     */
    @Override
    public void eliminarUsuario(Integer id) throws Exception {
        HttpClient.delete(
                BASE_URL + "/usuarios/eliminar/" + id,
                HEADER_USUARIO_ID,
                getUsuarioIdAutenticado().toString()
        );
    }

    /**
     * Crea una cadena JSON con los datos del usuario.
     *
     * @param usuario Objeto UsuarioFX del cual extraer los datos
     * @return Cadena con formato JSON para enviar al servidor
     */
    private String crearJsonUsuario(UsuarioFX usuario) {
        return String.format(
                "{\"nombre\": \"%s\", \"correo\": \"%s\", \"telefono\": \"%s\", \"contraseña\": \"%s\", \"idRol\": %d}",
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getContraseña(),
                usuario.getRol().getIdRol()
        );
    }

    /**
     * Procesa la respuesta JSON del servidor convirtiéndola en un objeto UsuarioFX.
     *
     * @param jsonResponse Respuesta JSON recibida del servidor
     * @return Objeto UsuarioFX con los datos de la respuesta
     * @throws Exception Si ocurre un error durante el procesamiento del JSON
     */
    private UsuarioFX procesarRespuestaUsuario(String jsonResponse) throws Exception {
        JsonNode usuarioNode = mapper.readTree(jsonResponse);
        return convertirJsonAUsuario(usuarioNode);
    }

    /**
     * Convierte un nodo JSON a un objeto UsuarioFX.
     *
     * @param usuarioNode Nodo JSON que contiene los datos del usuario
     * @return Objeto UsuarioFX con los datos extraídos del JSON
     */
    private UsuarioFX convertirJsonAUsuario(JsonNode usuarioNode) {
        JsonNode rolNode = usuarioNode.get("rol");
        RolFX rol = new RolFX(
                rolNode.get("idRol").asInt(),
                rolNode.get("rol").asText()
        );

        return new UsuarioFX(
                usuarioNode.get("idUsuario").asInt(),
                usuarioNode.get("nombre").asText(),
                usuarioNode.get("correo").asText(),
                usuarioNode.get("telefono").asText(),
                usuarioNode.get("contraseña").asText(),
                rol
        );
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado.
     *
     * @return ID del usuario autenticado
     */
    private Integer getUsuarioIdAutenticado() {
        return LoginServiceImplFX.getUsuarioActual().getIdUsuario();
    }
}