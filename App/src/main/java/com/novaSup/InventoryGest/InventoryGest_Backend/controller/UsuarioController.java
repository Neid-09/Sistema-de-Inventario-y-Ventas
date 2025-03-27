package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario, @RequestHeader("usuario-id") Integer idUsuario) {
        try {
            // Verificar si el usuario tiene permiso para crear usuarios
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "crear_usuario")) {
                return ResponseEntity.status(403).body("No tienes permisos para crear usuarios");
            }

            Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios(@RequestHeader("usuario-id") Integer idUsuario) {
        try {
            // Verificar si el usuario tiene permiso para ver usuarios
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "ver_usuarios")) {
                System.out.println("no tienes permisos para listar usuarios");
                return ResponseEntity.status(403).body("No tienes permisos para ver usuarios");
            }

            List<Usuario> usuarios = usuarioService.listarUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable int id,
            @RequestBody Usuario usuario,
            @RequestHeader("usuario-id") Integer idUsuario) {
        try {
            // Verificar si el usuario tiene permiso para editar usuarios
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "editar_usuario")) {
                return ResponseEntity.status(403).body("No tienes permisos para editar usuarios");
            }

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable int id,
            @RequestHeader("usuario-id") Integer idUsuario) {
        try {
            // Verificar si el usuario tiene permiso para eliminar usuarios
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "eliminar_usuario")) {
                return ResponseEntity.status(403).body("No tienes permisos para eliminar usuarios");
            }

            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> credentials) {
        try {
            String correo = credentials.get("correo");
            String contraseña = credentials.get("contraseña");

            if (correo == null || contraseña == null) {
                return ResponseEntity.badRequest().body("Correo y contraseña son obligatorios");
            }

            Usuario usuario = usuarioService.autenticarUsuario(correo, contraseña);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}