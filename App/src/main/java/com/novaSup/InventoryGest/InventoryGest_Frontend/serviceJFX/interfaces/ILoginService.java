package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

public interface ILoginService {
    boolean autenticarUsuario(String correo, String contraseña) throws Exception;
}