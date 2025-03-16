package com.novaSup.InventoryGest;

import com.novaSup.InventoryGest.utils.PathsFXML;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {

    private static ConfigurableApplicationContext springContext;
    private static Stage primaryStage;

    public static void main(String[] args) {
        /*Desactivar servidor web*/
        SpringApplication springApplication = new SpringApplication(MainApp.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);

        springContext = SpringApplication.run(MainApp.class, args);
        launch(args); // Iniciar JavaFX
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.MAIN_FXML));
        loader.setControllerFactory(springContext::getBean); // Inyecta controladores con Spring
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Mi App con JavaFX y Spring Boot");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close(); // Cerrar contexto de Spring al salir
    }
}
