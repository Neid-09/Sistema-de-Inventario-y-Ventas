# InventoryGest

Aplicación de escritorio desarrollada con Java 21 y JavaFX para la gestión de inventario.

## ✅ Requisitos

- **Java JDK 21**
- **JavaFX SDK 23.0.2**
- **Maven** (opcional, ya que este proyecto incluye el wrapper `mvnw` entonces debería funcionar sin problemas) 

## 🛠️ Configuración inicial

### 1. Descargar JavaFX SDK 23.0.2

Descárgalo desde [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/) y descomprímelo en una ruta local.  
[SOLO RECUENDENLA]
![Captura de pantalla 2025-03-25 215016](https://github.com/user-attachments/assets/f9861bee-29b7-4fcb-a5a4-4f4dc52ca730)

Recomendado: `C:libreias\java\javafx-sdk-23.0.2`

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
    - En la carpeta javafx-sdk-23.0.2 nos ubicamos en lib, y apartir de ahi copiamos la ruta
4. Ya configurado
  - ![image](https://github.com/user-attachments/assets/36e81190-a00b-4a25-9d2e-a56ee62557db)

### 3. Configurar la base de datos
## 🗃️ Base de Datos `appd2` – MySQL 9.2 (Uso con Workbench)

Este proyecto utiliza la base de datos local llamada **`appd2`**. A continuación, se detallan los pasos para importar y exportar la base de datos utilizando **MySQL Workbench**, sin necesidad de comandos de consola.

---

### 🧩 Requisitos

- Tener instalado **MySQL 9.2**
- Tener **MySQL Workbench** configurado y conectado al servidor local(Primero instalar MySQL y configurarlo)
- Contar con el archivo `backup_appd2.sql` (ubicado en la carpeta `/database` del proyecto)

---

### 📥 Importar base de datos en Workbench

1. Abre **MySQL Workbench** y conéctate a tu servidor local.(Si colocaron contraseña, se las pedira)
2. Crear una base de datos que se llame `appd2`
   => Comando sql para crear la base de datso:`CREATE DATABASE appd2`
   Actualizar con:
   Doble clic sobre la base de datos
3. En la barra superior, ve a **"SERVER" > "Data importa"**.
4. Marcar `Import from Self-Contained File` y ubicar la ruta del proyecto.
   Busca y selecciona el archivo:  
   `database/backup_appd2.sql`
5. Una vez seleccionada la ruta ir a Default Schema to be Imported To y seleccionar `appd2`.
6. Sola dar a `Star Import`.

---

### 📤 Exportar base de datos desde Workbench

1. En el panel izquierdo, haz clic d2 veces sobre `appd2`.
2. Ve a la barra superior: **"Server" > "Data Export"**.
3. En **"Schemas"**, selecciona `appd2`.
4. Marca:
   - ✅ *Export to Self-Contained File*
   - Ruta: `database/backup_appd2.sql`
5. Haz clic en **Start Export**.

---

### ⚠️ Recomendaciones

- **Siempre realiza un respaldo antes de modificar la base.**
- Comparte únicamente el archivo `backup_appd2.sql`.
- No subas archivos de sistema como `.ibd`, `.frm` o carpetas internas de MySQL.



### ==con eso deberia funcionar== 
### ==Para dudas consultar con el analista== 
