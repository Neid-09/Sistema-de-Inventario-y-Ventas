package com.novaSup.InventoryGest.InventoryGest_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
// Asegúrate de que WebApplicationType esté configurado como SERVLET o REACTIVE
// import org.springframework.boot.WebApplicationType;

@SpringBootApplication
@EnableScheduling // Mantener si el backend necesita tareas programadas
public class MainBackend {

    public static void main(String[] args) {
        // Iniciar Spring Boot como una aplicación web (servidor)
        // Si no se especifica, por defecto es SERVLET si spring-web está presente
        SpringApplication.run(MainBackend.class, args);
        System.out.println("Backend Spring Boot iniciado.");
        // Puedes añadir un log o mensaje indicando que el backend está listo
    }
}
