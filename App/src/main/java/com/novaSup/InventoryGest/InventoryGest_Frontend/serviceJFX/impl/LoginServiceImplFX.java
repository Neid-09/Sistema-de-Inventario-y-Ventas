package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.HashSet;
import java.util.Set;

public class LoginServiceImplFX implements ILoginService {

    private static final String BASE_URL = "http://localhost:8080";
    private static UsuarioFX usuarioActual;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean autenticarUsuario(String correo, String contraseña) throws Exception {
        try {
            String jsonBody = String.format(
                    "{\"correo\": \"%s\", \"contraseña\": \"%s\"}",
                    correo, contraseña
            );

            String response = HttpClient.post(BASE_URL + "/usuarios/login", jsonBody);

            // Procesar respuesta para obtener usuario con permisos
            JsonNode usuarioNode = mapper.readTree(response);
            JsonNode rolNode = usuarioNode.get("rol");

            Set<PermisoFX> permisosFX = new HashSet<>();
            if (rolNode.has("permisos")) {
                JsonNode permisosNode = rolNode.get("permisos");
                for (JsonNode permisoNode : permisosNode) {
                    permisosFX.add(new PermisoFX(
                            permisoNode.get("idPermiso").asInt(),
                            permisoNode.get("nombre").asText(),
                            permisoNode.get("descripcion").asText()
                    ));
                }
            }

            RolFX rol = new RolFX(
                    rolNode.get("idRol").asInt(),
                    rolNode.get("rol").asText(),
                    permisosFX
            );

            usuarioActual = new UsuarioFX(
                    usuarioNode.get("idUsuario").asInt(),
                    usuarioNode.get("nombre").asText(),
                    usuarioNode.get("correo").asText(),
                    usuarioNode.get("telefono").asText(),
                    usuarioNode.get("contraseña").asText(),
                    rol
            );

            return true; // Si llegamos aquí, la autenticación fue exitosa
        } catch (RuntimeException e) {
            if (e.getMessage().contains("401")) {
                return false;
            }
            throw e;
        }
    }

    public static UsuarioFX getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean tienePermiso(String nombrePermiso) {
        if (usuarioActual != null && usuarioActual.getRol() != null &&
                usuarioActual.getRol().getPermisos() != null) {
            return usuarioActual.getRol().getPermisos().stream()
                    .anyMatch(permiso -> permiso.getNombre().equals(nombrePermiso));
        }
        return false;
    }
}