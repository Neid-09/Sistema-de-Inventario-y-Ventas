package com.novaSup.InventoryGest.InventoryGest_Frontend.config;

import javafx.util.Callback;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Factoría de controladores personalizada para JavaFX.
 * Utiliza DependencyManager para obtener instancias de servicios requeridos por los constructores de los controladores.
 */
public class CustomControllerFactory implements Callback<Class<?>, Object> {

    private final DependencyManager dependencyManager;

    public CustomControllerFactory(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            // Intentar encontrar un constructor que podamos satisfacer con nuestros servicios
            // Dar prioridad a constructores con más argumentos (más específicos)
            Constructor<?>[] constructors = controllerClass.getDeclaredConstructors();
            // Usar lambda explícita para la comparación
            Arrays.sort(constructors, (c1, c2) -> Integer.compare(c2.getParameterCount(), c1.getParameterCount()));

            for (Constructor<?> constructor : constructors) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length == 0) {
                    // Constructor por defecto, instanciar directamente
                    return constructor.newInstance();
                }

                // Intentar resolver las dependencias para este constructor
                Object[] params = new Object[paramTypes.length];
                boolean canSatisfy = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    try {
                        // Obtener la instancia del servicio desde DependencyManager
                        params[i] = dependencyManager.getService(paramTypes[i]);
                    } catch (IllegalArgumentException e) {
                        // No se pudo encontrar un servicio para este parámetro
                        canSatisfy = false;
                        break; // Probar el siguiente constructor (si hay)
                    }
                }

                if (canSatisfy) {
                    // Encontramos un constructor cuyas dependencias podemos satisfacer
                    return constructor.newInstance(params);
                }
            }

            // Si llegamos aquí, no pudimos satisfacer ningún constructor
            throw new RuntimeException("No se pudo encontrar un constructor apropiado o satisfacer las dependencias para el controlador: " + controllerClass.getName());

        } catch (Exception e) {
            // Loggear el error podría ser útil aquí
            System.err.println("Error al instanciar el controlador " + controllerClass.getName() + ": " + e.getMessage());
            e.printStackTrace(); // Imprimir stack trace para depuración
            // Relanzar como RuntimeException para que FXMLLoader falle claramente
            throw new RuntimeException("No se pudo instanciar el controlador: " + controllerClass.getName(), e);
        }
    }
}
