package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// @Service // Anotación eliminada
public class LoginServiceImplFX implements ILoginService {

    private static final String LOGIN_URL = ApiConfig.getBaseUrl() + "/auth/login";
    private static String jwtToken = null;
    private static Set<String> permisos = new HashSet<>();
    private static String nombreUsuario = null;
    private static Integer idUsuario = null;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean autenticarUsuario(String correo, String contraseña) throws Exception {
        try {
            // Crear objeto JSON para la autenticación
            String jsonCredenciales = String.format("{\"correo\":\"%s\",\"contraseña\":\"%s\"}", correo, contraseña);

            // Realizar petición de autenticación
            String respuesta = HttpClient.post(LOGIN_URL, jsonCredenciales);
            JsonNode jsonNode = objectMapper.readTree(respuesta);

            // Extraer token y datos del usuario
            if (jsonNode.has("token") && jsonNode.has("usuario")) {
                jwtToken = jsonNode.get("token").asText();
                JsonNode usuarioNode = jsonNode.get("usuario");

                // Extraer nombre del usuario del objeto anidado
                if (usuarioNode.has("nombre")) {
                    nombreUsuario = usuarioNode.get("nombre").asText();
                } else {
                    nombreUsuario = correo; // Fallback al correo si no hay nombre
                }

                // Extraer ID del usuario del objeto anidado
                if (usuarioNode.has("idUsuario")) {
                    idUsuario = usuarioNode.get("idUsuario").asInt();
                } else {
                    idUsuario = null; // Asegurarse de que sea null si no se encuentra
                }

                // Extraer permisos del token JWT
                extraerPermisosDeToken(jwtToken);

                return true;
            } else if (jsonNode.has("message") && "La cuenta de usuario está inactiva.".equals(jsonNode.get("message").asText())) {
                // Manejo específico si la respuesta indica cuenta inactiva (aunque el backend ya devuelve 400)
                limpiarDatosAutenticacion();
                throw new Exception("La cuenta de usuario está inactiva.");
            } else {
                // Si no hay token o usuario, la autenticación falló
                limpiarDatosAutenticacion();
                // Intentar obtener un mensaje de error más específico si existe
                String errorMessage = "Credenciales inválidas o respuesta inesperada del servidor.";
                if (jsonNode.has("message")) {
                    errorMessage = jsonNode.get("message").asText();
                } else if (jsonNode.has("error")) {
                    errorMessage = jsonNode.get("error").asText();
                }
                throw new Exception(errorMessage);
            }

        } catch (Exception e) {
            // Limpiar datos en caso de error
            limpiarDatosAutenticacion();
            // Re-lanzar la excepción (puede ser la específica de cuenta inactiva o la genérica)
            // Verificar si el mensaje ya indica cuenta inactiva para no duplicar
            if (e.getMessage() != null && e.getMessage().contains("La cuenta de usuario está inactiva.")) {
                throw e; // Ya es la excepción correcta
            } else if (e.getMessage() != null && e.getMessage().contains("Credenciales inválidas")) {
                 throw new Exception("Credenciales inválidas."); // Mensaje más limpio
            }
            // Analizar el mensaje de la excepción original por si viene del HttpClient (ej. 400 Bad Request)
            // El HttpClient podría lanzar una excepción con el cuerpo de la respuesta en el mensaje
            String originalMessage = e.getMessage();
            if (originalMessage != null && originalMessage.contains("La cuenta de usuario está inactiva.")) {
                 throw new Exception("La cuenta de usuario está inactiva.");
            } else if (originalMessage != null && originalMessage.contains("Credenciales inválidas.")) {
                 throw new Exception("Credenciales inválidas.");
            }

            // Si no se pudo determinar la causa específica, lanzar error genérico
            throw new Exception("Error en la autenticación: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido"));
        }
    }

    /**
     * Extrae los permisos del token JWT decodificando su payload
     * @param token El token JWT
     */
    private void extraerPermisosDeToken(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length >= 2) {
                String payload = new String(Base64.getUrlDecoder().decode(partes[1]));
                JsonNode jsonNode = objectMapper.readTree(payload);

                // Limpiar permisos anteriores
                permisos.clear();

                // Extraer permisos del array "authorities"
                if (jsonNode.has("authorities") && jsonNode.get("authorities").isArray()) {
                    for (JsonNode nodo : jsonNode.get("authorities")) {
                        permisos.add(nodo.asText());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al extraer permisos del token: " + e.getMessage());
        }
    }

    /**
     * Verifica si el usuario tiene un permiso específico
     * @param nombrePermiso El nombre del permiso a verificar
     * @return true si el usuario tiene el permiso, false en caso contrario
     */
    public static boolean tienePermiso(String nombrePermiso) {
        // Si no hay permisos o token, no tiene acceso
        if (jwtToken == null || permisos == null || permisos.isEmpty()) {
            return false;
        }

        // Verificar si tiene el permiso específico
        return permisos.contains(nombrePermiso);
    }

    /**
     * Obtiene el token JWT actual
     * @return El token JWT o null si no hay sesión activa
     */
    public static String getToken() {
        return jwtToken;
    }

    /**
     * Obtiene el nombre del usuario autenticado
     * @return El nombre de usuario o null si no hay sesión activa
     */
    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Obtiene el ID del usuario autenticado
     * @return El ID del usuario o null si no está disponible
     */
    public static Integer getIdUsuario() {
        return idUsuario;
    }

    /**
     * Cierra la sesión actual limpiando todos los datos de autenticación
     */
    public static void cerrarSesion() {
        limpiarDatosAutenticacion();
    }

    /**
     * Limpia todos los datos de autenticación
     */
    private static void limpiarDatosAutenticacion() {
        jwtToken = null;
        nombreUsuario = null;
        idUsuario = null;
        permisos = new HashSet<>();
    }

    /**
     * Obtiene la lista de permisos del usuario actual
     * @return Conjunto no modificable de permisos
     */
    public static Set<String> getPermisos() {
        return Collections.unmodifiableSet(permisos);
    }

    /**
     * Obtiene la información del usuario actualmente autenticado
     * @return Objeto UsuarioFX con los datos disponibles del usuario actual o null si no hay sesión
     */
    public static UsuarioFX getUsuarioActual() {
        if (nombreUsuario == null || idUsuario == null) {
            return null;
        }

        // Crear instancia con la información disponible (ID y Nombre real)
        UsuarioFX usuario = new UsuarioFX();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nombreUsuario); // Usar el nombre real obtenido
        // El correo no se almacena directamente ahora, pero podría añadirse si fuera necesario
        // Otros campos como rol, estado, etc., no se obtienen en el login y requerirían otra llamada API
        // o ser incluidos en la respuesta del login.

        return usuario;
    }
}