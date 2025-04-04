package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class LoginServiceImplFX implements ILoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImplFX.class);
    private static final String BASE_URL = "http://localhost:8080";
    private static UsuarioFX usuarioActual = null;
    private static Set<String> permisosUsuario = new HashSet<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private static String token;

    @Override
    public boolean autenticarUsuario(String correo, String contraseña) throws Exception {
        try {
            ObjectNode credenciales = mapper.createObjectNode();
            credenciales.put("correo", correo);
            credenciales.put("contraseña", contraseña);

            String response = HttpClient.post(
                    BASE_URL + "/auth/login",
                    mapper.writeValueAsString(credenciales)
            );

            JsonNode jsonResponse = mapper.readTree(response);

            // Extraer y guardar el token JWT
            if (jsonResponse.has("token")) {
                token = jsonResponse.get("token").asText();
                logger.info("Token JWT recibido y almacenado");
            } else {
                token = null;
                logger.warn("No se recibió token en la respuesta de autenticación");
            }

            // Continuar con el procesamiento del usuario
            if (jsonResponse.has("usuario")) {
                JsonNode usuarioNode = jsonResponse.get("usuario");

                // Crear objeto de usuario
                Integer idUsuario = usuarioNode.has("idUsuario") ? usuarioNode.get("idUsuario").asInt() : null;
                String nombre = usuarioNode.has("nombre") ? usuarioNode.get("nombre").asText() : "";
                String correoUsuario = usuarioNode.has("correo") ? usuarioNode.get("correo").asText() : "";
                String telefono = usuarioNode.has("telefono") ? usuarioNode.get("telefono").asText() : "";

                // Procesar rol del usuario
                RolFX rol = null;
                if (usuarioNode.has("rol") && !usuarioNode.get("rol").isNull()) {
                    JsonNode rolNode = usuarioNode.get("rol");
                    Integer idRol = rolNode.has("idRol") ? rolNode.get("idRol").asInt() : null;
                    String nombreRol = rolNode.has("nombre") ? rolNode.get("nombre").asText() : "";
                    rol = new RolFX(idRol, nombreRol);
                }

                // Crear y almacenar el usuario actual
                usuarioActual = new UsuarioFX(idUsuario, nombre, correoUsuario, telefono, "", rol);

                // Cargar permisos del usuario
                cargarPermisosUsuario();

                return true;
            }

            return false;
        } catch (Exception e) {
            token = null;
            usuarioActual = null;
            permisosUsuario.clear();
            logger.error("Error en autenticación: {}", e.getMessage());
            throw new Exception("Error al autenticar: " + e.getMessage());
        }
    }

    public static String getToken() {
        return token;
    }

    /**
     * Carga los permisos del usuario actual desde el backend
     */
    private void cargarPermisosUsuario() throws Exception {
        if (usuarioActual == null || usuarioActual.getIdUsuario() == null) {
            logger.warn("No hay usuario autenticado para cargar permisos");
            permisosUsuario.clear();
            return;
        }

        try {
            // Usar el token JWT para autenticar la solicitud
            String response = HttpClient.get(BASE_URL + "/usuarios/mis-permisos");

            // Procesar permisos (incluye permisos del rol y específicos)
            JsonNode permisosNode = mapper.readTree(response);
            permisosUsuario.clear();

            for (JsonNode permisoNode : permisosNode) {
                if (permisoNode.has("nombre")) {
                    String nombrePermiso = permisoNode.get("nombre").asText();
                    permisosUsuario.add(nombrePermiso);
                }
            }

            logger.info("Permisos cargados para {}: {}", usuarioActual.getNombre(), permisosUsuario);
        } catch (Exception e) {
            logger.error("Error al cargar permisos: {}", e.getMessage());
            throw new Exception("Error al cargar permisos: " + e.getMessage());
        }
    }

    /**
     * Añade permisos comunes para administradores
     */
    private void agregarPermisosAdministrador() {
        // Permisos de gestión de usuarios
        permisosUsuario.add("gestionar_usuarios");
        permisosUsuario.add("ver_usuarios");
        permisosUsuario.add("crear_usuario");
        permisosUsuario.add("editar_usuario");
        permisosUsuario.add("eliminar_usuario");

        // Permisos de gestión de inventario
        permisosUsuario.add("gestionar_inventario");
        permisosUsuario.add("ver_productos");
        permisosUsuario.add("crear_producto");
        permisosUsuario.add("editar_producto");
        permisosUsuario.add("eliminar_producto");

        // Permisos de ventas
        permisosUsuario.add("gestionar_ventas");
        permisosUsuario.add("crear_venta");
        permisosUsuario.add("ver_ventas");
        permisosUsuario.add("anular_venta");

        // Permisos de informes
        permisosUsuario.add("ver_informes");
        permisosUsuario.add("generar_informes");

        // Permisos de configuración
        permisosUsuario.add("gestionar_permisos");
        permisosUsuario.add("gestionar_roles");
        permisosUsuario.add("configurar_sistema");

        logger.info("Permisos de administrador cargados para: {}", usuarioActual.getNombre());
    }

    /**
     * Verifica si el usuario actual tiene un permiso específico
     * @param nombrePermiso Nombre del permiso a verificar
     * @return true si tiene el permiso, false en caso contrario
     */
    public static boolean tienePermiso(String nombrePermiso) {
        // Si no hay usuario autenticado, no tiene permisos
        if (usuarioActual == null) {
            return false;
        }

        // Si el usuario es administrador, tiene todos los permisos
        if (usuarioActual.getRol() != null &&
                "Administrador".equalsIgnoreCase(usuarioActual.getRol().getNombre())) {
            return true;
        }

        // Verificar si el permiso está en la lista de permisos del usuario
        return permisosUsuario.contains(nombrePermiso);
    }

    /**
     * Verifica un permiso específico consultando al backend
     * Útil para operaciones críticas donde necesitamos verificación en tiempo real
     */
    public static boolean verificarPermisoEnBackend(String nombrePermiso) throws Exception {
        if (usuarioActual == null || usuarioActual.getIdUsuario() == null) {
            return false;
        }

        try {
            // Consultar al backend para verificar el permiso
            String response = HttpClient.get(
                    BASE_URL + "/usuarios/verificar-permiso/" + nombrePermiso,
                    "usuario-id",
                    usuarioActual.getIdUsuario().toString()
            );

            // Si la respuesta no contiene errores, tiene permiso
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response);

            if (jsonResponse.has("tienePermiso")) {
                return jsonResponse.get("tienePermiso").asBoolean();
            }

            return true; // Si no hay respuesta estructurada, asumir que tiene permiso
        } catch (Exception e) {
            // Si el error es 403, significa que no tiene permiso
            if (e.getMessage().contains("code: 403")) {
                return false;
            }

            // Para otros errores, propagar la excepción
            throw e;
        }
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    public static UsuarioFX getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public static void cerrarSesion() {
        usuarioActual = null;
        permisosUsuario.clear();
        logger.info("Sesión cerrada");
    }
}