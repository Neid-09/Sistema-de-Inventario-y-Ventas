# InventoryGest

Aplicación de escritorio desarrollada con Java 21 y JavaFX para la gestión de inventario.

## ✅ Requisitos

- **Java JDK 21**
- **JavaFX SDK 23.0.2**
- **Maven** (opcional, ya que este proyecto incluye el wrapper `mvnw` entonces debería funcionar sin problemas) 

## 🛠️ Configuración inicial

### 1. Descargar JavaFX SDK 23.0.2

Descárgalo desde [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/) y descomprímelo en una ruta local.  
![Captura de pantalla 2025-03-25 215016](https://github.com/user-attachments/assets/f9861bee-29b7-4fcb-a5a4-4f4dc52ca730)

Recomendado: `C:\javafx-sdk-23.0.2`

---

### 2. Configurar la ejecución en IntelliJ (Para este proyecto cambiar si tienen otra version de java a la 21)
- **Cuando se inie por primera vez el proyecto aceptar todo lo que aparece (en intellij no se en otros IDE) son dependencias**
- **Una vez descargadas las dependencias ir a MainApp y hacer la configuration de arranque**
#### 🧩 Ir a: `Run > Edit Configurations...`
![Captura de pantalla 2025-03-25 215121](https://github.com/user-attachments/assets/104c4eff-9284-4bbe-adc7-44ebd4cfd1b6)


1. Selecciona tu configuración de arranque(Deberia de estar) o crea una nueva de tipo **Application**.
    - ![Captura de pantalla 2025-03-25 220616](https://github.com/user-attachments/assets/7ccccc58-dc04-4dc5-8e0a-dd9399e3aaaa)

2. Establece:
    - **Main class**: `com.novaSup.InventoryGest.MainApp`
3. En el campo **VM options**, añade: 

    - **VM OPTIONS**: `--module-path "LA RUTA DEL SDK en comillas" --add-modules javafx.controls,javafx.fxml`.
### ==con eso deberia funcionar== 
### ==Para dudas consultar con el analista== 
