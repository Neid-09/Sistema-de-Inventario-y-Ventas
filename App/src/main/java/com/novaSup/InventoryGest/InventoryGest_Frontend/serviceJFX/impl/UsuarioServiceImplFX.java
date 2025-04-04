package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsuarioServiceImplFX implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImplFX.class);
    private static final String BASE_URL = "http://localhost:8080/usuarios";
    private static final String ROLES_URL = "http://localhost:8080/roles";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<RolFX> obtenerRoles() throws Exception {
        try {
            String response = HttpClient.get(ROLES_URL);
            JsonNode rolesNode = mapper.readTree(response);
            List<RolFX> roles = new ArrayList<>();

            for (JsonNode rolNode : rolesNode) {
                try {
                    RolFX rol = convertirRolDeJSON(rolNode);
                    roles.add(rol);
                } catch (Exception e) {
                    logger.error("Error procesando rol: {}", e.getMessage());
                }
            }
            return roles;
        } catch (Exception e) {
            logger.error("Error al obtener roles: {}", e.getMessage());
            throw new Exception("Error al obtener roles: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioFX> obtenerUsuarios() throws Exception {
        try {
            String response = HttpClient.get(BASE_URL);
            JsonNode usuariosNode = mapper.readTree(response);
            List<UsuarioFX> usuarios = new ArrayList<>();

            for (JsonNode usuarioNode : usuariosNode) {
                try {
                    UsuarioFX usuario = convertirUsuarioDeJSON(usuarioNode);
                    usuarios.add(usuario);
                } catch (Exception e) {
                    logger.error("Error procesando usuario: {}", e.getMessage());
                }
            }
            return usuarios;
        } catch (Exception e) {
            logger.error("Error al obtener usuarios: {}", e.getMessage());
            throw new Exception("Error al obtener usuarios: " + e.getMessage());
        }
    }

    @Override
    public UsuarioFX registrarUsuario(UsuarioFX usuario) throws Exception {
        try {
            ObjectNode usuarioDTO = mapper.createObjectNode();
            usuarioDTO.put("nombre", usuario.getNombre());
            usuarioDTO.put("correo", usuario.getCorreo());
            usuarioDTO.put("telefono", usuario.getTelefono());
            usuarioDTO.put("contraseña", usuario.getContraseña());

            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                ObjectNode rolNode = usuarioDTO.putObject("rol");
                rolNode.put("idRol", usuario.getRol().getIdRol());
            }

            String response = HttpClient.post(
                    BASE_URL + "/registrar",
                    mapper.writeValueAsString(usuarioDTO)
            );

            JsonNode usuarioNode = mapper.readTree(response);
            return convertirUsuarioDeJSON(usuarioNode);
        } catch (Exception e) {
            logger.error("Error al registrar usuario: {}", e.getMessage());
            throw new Exception("Error al registrar usuario: " + e.getMessage());
        }
    }

    @Override
    public UsuarioFX actualizarUsuario(Integer id, UsuarioFX usuario) throws Exception {
        try {
            ObjectNode usuarioDTO = mapper.createObjectNode();
            usuarioDTO.put("nombre", usuario.getNombre());
            usuarioDTO.put("correo", usuario.getCorreo());
            usuarioDTO.put("telefono", usuario.getTelefono());

            // Solo incluir contraseña si no está vacía
            if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
                usuarioDTO.put("contraseña", usuario.getContraseña());
            }

            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                ObjectNode rolNode = usuarioDTO.putObject("rol");
                rolNode.put("idRol", usuario.getRol().getIdRol());
            }

            String response = HttpClient.put(
                    BASE_URL + "/" + id,
                    mapper.writeValueAsString(usuarioDTO)
            );

            JsonNode usuarioNode = mapper.readTree(response);
            return convertirUsuarioDeJSON(usuarioNode);
        } catch (Exception e) {
            logger.error("Error al actualizar usuario: {}", e.getMessage());
            throw new Exception("Error al actualizar usuario: " + e.getMessage());
        }
    }

    @Override
    public void eliminarUsuario(Integer id) throws Exception {
        try {
            HttpClient.delete(BASE_URL + "/" + id);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage());
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @Override
    public Set<PermisoFX> obtenerPermisosEspecificos(Integer idUsuario) throws Exception {
        try {
            // Usar el endpoint correcto
            String response = HttpClient.get(BASE_URL + "/" + idUsuario + "/permisos-disponibles");
            JsonNode responseNode = mapper.readTree(response);
            Set<PermisoFX> permisos = new HashSet<>();

            // Obtenemos directamente los permisos personalizados del endpoint
            if (responseNode.has("permisosPersonalizados")) {
                JsonNode permisosNode = responseNode.get("permisosPersonalizados");

                for (JsonNode permisoNode : permisosNode) {
                    Integer idPermiso = permisoNode.has("idPermiso") ? permisoNode.get("idPermiso").asInt() : null;
                    String nombre = permisoNode.has("nombre") ? permisoNode.get("nombre").asText() : "";
                    String descripcion = permisoNode.has("descripcion") ? permisoNode.get("descripcion").asText() : "";

                    permisos.add(new PermisoFX(idPermiso, nombre, descripcion));
                }
            }

            return permisos;
        } catch (Exception e) {
            logger.error("Error al obtener permisos específicos: {}", e.getMessage());
            throw new Exception("Error al obtener permisos específicos: " + e.getMessage());
        }
    }

    @Override
    public void asignarPermisosEspecificos(Integer idUsuario, List<Integer> idsPermisos) throws Exception {
        try {
            ArrayNode permisosArray = mapper.createArrayNode();
            for (Integer id : idsPermisos) {
                permisosArray.add(id);
            }

            // Cambiar la URL para que coincida con el endpoint del controlador
            HttpClient.put(
                    BASE_URL + "/" + idUsuario + "/permisos",
                    mapper.writeValueAsString(permisosArray)
            );
        } catch (Exception e) {
            logger.error("Error al asignar permisos específicos: {}", e.getMessage());
            throw new Exception("Error al asignar permisos específicos: " + e.getMessage());
        }
    }

    @Override
    public Set<PermisoFX> obtenerPermisosRol(Integer idRol) throws Exception {
        try {
            String response = HttpClient.get(ROLES_URL + "/" + idRol + "/permisos");
            JsonNode permisosNode = mapper.readTree(response);
            Set<PermisoFX> permisos = new HashSet<>();

            for (JsonNode permisoNode : permisosNode) {
                Integer idPermiso = permisoNode.has("idPermiso") ? permisoNode.get("idPermiso").asInt() : null;
                String nombre = permisoNode.has("nombre") ? permisoNode.get("nombre").asText() : "";
                String descripcion = permisoNode.has("descripcion") ? permisoNode.get("descripcion").asText() : "";

                permisos.add(new PermisoFX(idPermiso, nombre, descripcion));
            }

            return permisos;
        } catch (Exception e) {
            logger.error("Error al obtener permisos del rol: {}", e.getMessage());
            throw new Exception("Error al obtener permisos del rol: " + e.getMessage());
        }
    }

    private UsuarioFX convertirUsuarioDeJSON(JsonNode usuarioNode) {
        Integer idUsuario = usuarioNode.has("idUsuario") ? usuarioNode.get("idUsuario").asInt() : null;
        String nombre = usuarioNode.has("nombre") ? usuarioNode.get("nombre").asText() : "";
        String correo = usuarioNode.has("correo") ? usuarioNode.get("correo").asText() : "";
        String telefono = usuarioNode.has("telefono") ? usuarioNode.get("telefono").asText() : "";

        // La contraseña no se devuelve por seguridad
        String contraseña = "";

        // Procesar rol si está incluido
        RolFX rol = null;
        if (usuarioNode.has("rol") && !usuarioNode.get("rol").isNull()) {
            JsonNode rolNode = usuarioNode.get("rol");
            rol = convertirRolDeJSON(rolNode);
        }

        return new UsuarioFX(idUsuario, nombre, correo, telefono, contraseña, rol);
    }

    private RolFX convertirRolDeJSON(JsonNode rolNode) {
        Integer idRol = rolNode.has("idRol") ? rolNode.get("idRol").asInt() : null;
        String nombre = rolNode.has("nombre") ? rolNode.get("nombre").asText() : "";

        return new RolFX(idRol, nombre);
    }
}