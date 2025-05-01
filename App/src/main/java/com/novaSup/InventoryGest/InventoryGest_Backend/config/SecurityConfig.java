package com.novaSup.InventoryGest.InventoryGest_Backend.config;

import com.novaSup.InventoryGest.InventoryGest_Backend.security.jwt.JwtAuthenticationFilter;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**PARA LOS PERMISOS TENER EN CUENTA QUE EL ADMIN PUEDE HACER LO QUE QUIERA
 * PERO EL VENDEDOR SE LIMITA A ESTO:
 *   'ver_productos',
 *   'crear_venta',
 *   'ver_ventas',
 *   'generar_factura',
 *   'ver_facturas',
 *   'registrar_pago_venta',
 *   'ver_promociones',
 *   'ver_recompensas',
 *   'registrar_devolucion',
 *   'ver_devoluciones',
 *   'otorgar_credito',
 *   'ver_creditos',
 *   'registrar_pago_credito',
 *   'ver_clientes',
 *   'gestionar_clientes',
 *   'ver_notificaciones'
 **/

/**PERMISOS POR SECCIONES(TODOS)
 *
 * -- Gestión de Usuarios y Roles
 * ('ver_usuarios', 'Permite ver la lista de usuarios del sistema'),
 * ('crear_usuario', 'Permite registrar un nuevo usuario'),
 * ('editar_usuario', 'Permite modificar los datos de un usuario'),
 * ('eliminar_usuario', 'Permite eliminar usuarios del sistema'),
 * ('asignar_roles', 'Permite asignar roles a los usuarios'),
 * ('gestionar_roles_permisos', 'Permite administrar permisos asociados a los roles'),
 *
 * -- Gestión de Productos
 * ('ver_productos', 'Permite consultar los productos disponibles en el inventario'),
 * ('crear_producto', 'Permite agregar un nuevo producto al inventario'),
 * ('editar_producto', 'Permite modificar la información de un producto existente'),
 * ('eliminar_producto', 'Permite eliminar productos del inventario'),
 * ('ver_categorias', 'Permite consultar las categorías de productos'),
 * ('gestionar_impuestos', 'Permite gestionar los impuestos aplicables a los productos'),
 *
 * -- Entradas y Proveedores
 * ('registrar_entrada_producto', 'Permite registrar el ingreso de productos al inventario'),
 * ('ver_entradas_productos', 'Permite consultar el historial de entradas de productos'),
 * ('ver_proveedores', 'Permite ver la lista de proveedores'),
 * ('gestionar_proveedores', 'Permite agregar, editar o eliminar proveedores'),
 *
 * -- Ventas y Facturación
 * ('crear_venta', 'Permite registrar una nueva venta'),
 * ('ver_ventas', 'Permite consultar el historial de ventas'),
 * ('generar_factura', 'Permite generar facturas de ventas'),
 * ('ver_facturas', 'Permite consultar las facturas generadas'),
 * ('gestionar_metodos_pago', 'Permite agregar o modificar métodos de pago'),
 * ('registrar_pago_venta', 'Permite registrar pagos realizados por ventas'),
 *
 * -- Promociones, Recompensas y Devoluciones
 * ('ver_promociones', 'Permite consultar promociones disponibles'),
 * ('crear_promocion', 'Permite crear nuevas promociones'),
 * ('ver_recompensas', 'Permite consultar recompensas otorgadas a clientes'),
 * ('registrar_devolucion', 'Permite registrar la devolución de productos'),
 * ('ver_devoluciones', 'Permite ver el historial de devoluciones'),
 *
 * -- Créditos y Garantías
 * ('otorgar_credito', 'Permite asignar créditos a clientes'),
 * ('ver_creditos', 'Permite consultar los créditos otorgados'),
 * ('registrar_pago_credito', 'Permite registrar pagos de créditos'),
 * ('gestionar_garantias', 'Permite registrar y consultar garantías'),
 *
 * -- Vendedores y Clientes
 * ('ver_clientes', 'Permite consultar la lista de clientes'),
 * ('gestionar_clientes', 'Permite agregar o editar información de clientes'),
 * ('ver_vendedores', 'Permite consultar la lista de vendedores'),
 *
 * -- Auditorías y Caja
 * ('ver_auditorias', 'Permite consultar las auditorías generales del sistema'),
 * ('auditar_stock', 'Permite auditar el stock de productos'),
 * ('gestionar_caja', 'Permite abrir, cerrar y gestionar la caja'),
 * ('ver_auditoria_caja', 'Permite ver auditorías realizadas a la caja'),
 *
 * -- Comisiones y Configuración
 * ('ver_comisiones', 'Permite consultar las comisiones generadas por ventas'),
 * ('gestionar_configuracion', 'Permite cambiar las configuraciones generales del sistema'),
 *
 * -- Notificaciones y Sesiones
 * ('ver_notificaciones', 'Permite consultar las notificaciones del sistema'),
 * ('gestionar_sesiones', 'Permite ver o gestionar sesiones activas'),
 *
 * -- Servicios
 * ('ver_servicios', 'Permite ver los servicios ofrecidos'),
 * ('gestionar_servicios', 'Permite crear o editar servicios');*/


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}