package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Rol;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.jwt.JwtTokenProvider;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.service.CustomUserDetailsService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.UsuarioService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException; // Importar DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(), loginRequest.getContraseña())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCorreo());

            // No es necesario verificar el estado aquí explícitamente,
            // ya que CustomUserDetails lo maneja y AuthenticationManager
            // lanzará DisabledException si el usuario no está activo.

            Usuario usuario = userDetailsService.getUserByEmail(loginRequest.getCorreo());

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", usuario.getIdUsuario());
            claims.put("nombre", usuario.getNombre());
            claims.put("rol", usuario.getRol().getNombre());
            claims.put("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            String jwt = jwtTokenProvider.generateToken(userDetails, claims);

            return ResponseEntity.ok(new AuthResponse(jwt, usuario));

        } catch (DisabledException e) { // Capturar específicamente la excepción de usuario deshabilitado
            return ResponseEntity.badRequest().body("La cuenta de usuario está inactiva.");
        } catch (Exception e) { // Capturar otras excepciones de autenticación
            return ResponseEntity.badRequest().body("Credenciales inválidas.");
        }
    }

    // CUIDADO CON EL REGISTRO DE USUARIOS, PUEDE CREAR UN USUARIO ADMINISTRADOR SIN AUTENTICACIÓN
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Verificar si ya existe un usuario con ese correo
            if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
                return ResponseEntity.badRequest().body("Ya existe un usuario con ese correo");
            }

            // Establecer el estado del usuario como activo por defecto
            usuario.setEstado(true);

            // Si no se especifica un rol, asignar el rol por defecto
            if (usuario.getIdRol() == null) {
                // Si es el primer usuario, asignar rol de Administrador
                if (usuarioRepository.count() == 0) {
                    Rol rolAdmin = rolRepository.findByNombre("Administrador")
                            .orElseThrow(() -> new RuntimeException("Rol de Administrador no encontrado"));
                    usuario.setRol(rolAdmin);
                } else {
                    // Si no es el primer usuario, asignar rol de Vendedor por defecto
                    Rol rolVendedor = rolRepository.findByNombre("Vendedor")
                            .orElseThrow(() -> new RuntimeException("Rol de Vendedor no encontrado"));
                    usuario.setRol(rolVendedor);
                }
            } else {
                // Si se especificó un rol, usarlo
                Rol rol = rolRepository.findById(usuario.getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                usuario.setRol(rol);
            }

            return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String correo;
        private String contraseña;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private Usuario usuario;
    }
}