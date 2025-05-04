package com.novaSup.InventoryGest.InventoryGest_Frontend.config;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.*;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.*;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona las instancias de los servicios (dependencias) para la aplicación frontend.
 * Reemplaza la necesidad de un contenedor de inyección de dependencias como Spring en el frontend.
 */
public class DependencyManager {

    private final Map<Class<?>, Object> services = new HashMap<>();
    private Callback<Class<?>, Object> controllerFactory; // Referencia a la factory

    public DependencyManager() {
        // Crear e registrar instancias de los servicios aquí
        // Usa las implementaciones FX que se comunican con el backend (HTTP)
        services.put(ILoginService.class, new LoginServiceImplFX()); // Usar ILoginService
        services.put(INotificacionService.class, new NotificacionServiceImplFX());
        services.put(IProductoService.class, new ProductoServiceImplFX()); // Asumiendo que existe
        services.put(ILoteService.class, new LoteServiceImplFX()); // Asumiendo que existe
        services.put(ICategoriaService.class, new CategoriaServiceImplFX()); // Asumiendo que existe
        services.put(IProveedorService.class, new ProveedorServiceImplFX()); // Asumiendo que existe
        services.put(IInventarioService.class, new InventarioServiceImplFX()); // Asumiendo que existe
        services.put(IUsuarioService.class, new UsuarioServiceImplFX()); // Asumiendo que existe
        services.put(IRolService.class, new RolServiceImplFX()); // Asumiendo que existe
        services.put(IPermisoService.class, new PermisoServiceImplFX()); // Asumiendo que existe
        services.put(IConfiguracionServiceFX.class, new ConfiguracionServiceImplFX()); // Registrar el nuevo servicio
        services.put(IRegistMovimientService.class, new RegistMovimientServiceImplFX()); // Registrar movimiento de inventario
        services.put(IClienteService.class, new ClienteServiceImplFX()); // Registrar movimiento de inventario

        // ... Agrega aquí todas las interfaces y sus implementaciones FX correspondientes
    }

    // Método para establecer la factory DESPUÉS de que se haya creado
    public void setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
        // Registrar la factory como un "servicio" inyectable
        // Usamos Callback.class como clave genérica
        services.put(Callback.class, controllerFactory);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        // Si se solicita específicamente la factory (Callback.class), devolverla
        if (serviceClass == Callback.class && this.controllerFactory != null) {
            return (T) this.controllerFactory;
        }

        T service = (T) services.get(serviceClass);
        if (service == null) {
            // Añadir una comprobación específica para la factory si aún no se ha establecido
            if (serviceClass == Callback.class) {
                 throw new IllegalStateException("ControllerFactory aún no ha sido establecida en DependencyManager.");
            }
            throw new IllegalArgumentException("Servicio no registrado en DependencyManager: " + serviceClass.getName());
        }
        return service;
    }

    // Puedes añadir métodos específicos si necesitas configuraciones especiales para algún servicio
}
