package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.ArrayList;
import java.util.List;

public class UsuarioServiceImplFX implements IUsuarioService {

    private static final String BASE_URL = "http://localhost:8080";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<RolFX> obtenerRoles() throws Exception {
        String jsonResponse = HttpClient.get(BASE_URL + "/roles");

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

    @Override
    public List<UsuarioFX> obtenerUsuarios() throws Exception {
        String jsonResponse = HttpClient.get(BASE_URL + "/usuarios/listar");

        List<UsuarioFX> usuarios = new ArrayList<>();
        JsonNode usuariosArray = mapper.readTree(jsonResponse);

        for (JsonNode usuarioNode : usuariosArray) {
            JsonNode rolNode = usuarioNode.get("rol");
            RolFX rol = new RolFX(
                    rolNode.get("idRol").asInt(),
                    rolNode.get("rol").asText()
            );

            UsuarioFX usuario = new UsuarioFX(
                    usuarioNode.get("idUsuario").asInt(),
                    usuarioNode.get("nombre").asText(),
                    usuarioNode.get("correo").asText(),
                    usuarioNode.get("telefono").asText(),
                    usuarioNode.get("contraseña").asText(),
                    rol
            );

            usuarios.add(usuario);
        }

        return usuarios;
    }

    @Override
    public UsuarioFX registrarUsuario(UsuarioFX usuario) throws Exception {
        String jsonBody = String.format(
                "{\"nombre\": \"%s\", \"correo\": \"%s\", \"telefono\": \"%s\", \"contraseña\": \"%s\", \"idRol\": %d}",
                usuario.getNombre(), usuario.getCorreo(), usuario.getTelefono(),
                usuario.getContraseña(), usuario.getRol().getIdRol()
        );

        String jsonResponse = HttpClient.post(BASE_URL + "/usuarios/registrar", jsonBody);
        return procesarRespuestaUsuario(jsonResponse);
    }

    @Override
    public UsuarioFX actualizarUsuario(Integer id, UsuarioFX usuario) throws Exception {
        String jsonBody = String.format(
                "{\"nombre\": \"%s\", \"correo\": \"%s\", \"telefono\": \"%s\", \"contraseña\": \"%s\", \"idRol\": %d}",
                usuario.getNombre(), usuario.getCorreo(), usuario.getTelefono(),
                usuario.getContraseña(), usuario.getRol().getIdRol()
        );

        String jsonResponse = HttpClient.put(BASE_URL + "/usuarios/actualizar/" + id, jsonBody);
        return procesarRespuestaUsuario(jsonResponse);
    }

    @Override
    public void eliminarUsuario(Integer id) throws Exception {
        HttpClient.delete(BASE_URL + "/usuarios/eliminar/" + id);
    }

    private UsuarioFX procesarRespuestaUsuario(String jsonResponse) throws Exception {
        JsonNode usuarioNode = mapper.readTree(jsonResponse);
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
}