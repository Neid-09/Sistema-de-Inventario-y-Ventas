package com.novaSup.InventoryGest.InventoryGest_Frontend;

import com.novaSup.InventoryGest.InventoryGest_Frontend.config.CustomControllerFactory; // Importar la nueva factory
import com.novaSup.InventoryGest.InventoryGest_Frontend.config.DependencyManager; // Importar el gestor de dependencias
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback; // Importar Callback

public class MainFrontend extends Application {

    // Eliminar el campo primaryStage no utilizado
    // private static Stage primaryStage;
    private DependencyManager dependencyManager; // Instancia del gestor
    private Callback<Class<?>, Object> controllerFactory; // Instancia de la factory

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        // Inicializar el gestor de dependencias y la factory ANTES de start()
        dependencyManager = new DependencyManager();
        controllerFactory = new CustomControllerFactory(dependencyManager);
        // Establecer la factory en el gestor para que pueda ser inyectada
        dependencyManager.setControllerFactory(controllerFactory);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Usar la variable local 'stage' en lugar de primaryStage
        FXMLLoader loader = new FXMLLoader(MainFrontend.class.getResource(PathsFXML.LOGIN_FXML));

        // Establecer la factoría de controladores personalizada
        loader.setControllerFactory(controllerFactory);

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("InventoryGest - Cliente");
        stage.show();

    }

    // El método stop() que cerraba springContext ya no es necesario aquí.
}