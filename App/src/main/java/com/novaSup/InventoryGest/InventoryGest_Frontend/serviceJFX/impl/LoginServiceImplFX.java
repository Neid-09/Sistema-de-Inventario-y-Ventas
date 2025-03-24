package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

public class LoginServiceImplFX implements ILoginService {

    private static final String BASE_URL = "http://localhost:8080";

    @Override
    public boolean autenticarUsuario(String correo, String contraseña) throws Exception {
        try {
            String jsonBody = String.format(
                    "{\"correo\": \"%s\", \"contraseña\": \"%s\"}",
                    correo, contraseña
            );

            // La petición exitosa devolverá una respuesta que podemos procesar
            String response = HttpClient.post(BASE_URL + "/usuarios/login", jsonBody);
            return true; // Si llegamos aquí, la autenticación fue exitosa
        } catch (RuntimeException e) {
            // Si el código de respuesta es 401, las credenciales son incorrectas
            if (e.getMessage().contains("401")) {
                return false;
            }
            // Cualquier otro error se propaga
            throw e;
        }
    }
}