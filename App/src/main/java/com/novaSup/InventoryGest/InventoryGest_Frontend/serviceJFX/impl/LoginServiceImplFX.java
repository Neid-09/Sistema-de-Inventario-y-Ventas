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

            // Extraer token
            if (jsonNode.has("token")) {
                jwtToken = jsonNode.get("token").asText();
                nombreUsuario = correo;

                // Extraer permisos del token JWT
                extraerPermisosDeToken(jwtToken);

                // Extraer ID del usuario si está disponible
                if (jsonNode.has("idUsuario")) {
                    idUsuario = jsonNode.get("idUsuario").asInt();
                }

                return true;
            }

            return false;
        } catch (Exception e) {
            // Limpiar datos en caso de error
            limpiarDatosAutenticacion();
            throw new Exception("Error en la autenticación: " + e.getMessage());
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

        // Crear instancia con la información disponible
        UsuarioFX usuario = new UsuarioFX();
        usuario.setIdUsuario(idUsuario);
        usuario.setCorreo(nombreUsuario);
        // Otros campos se dejan vacíos o nulos porque no están disponibles

        return usuario;
    }
}