# Reto 4 - Tres en Raya (Menus y Diálogos) 🎮

Este repositorio contiene la solución al **Reto 4**, la cual es una evolución del desarrollo previo del Tres en Raya (basado en el código base del Reto 3), integrando elementos de interfaz de usuario más avanzados en Android como **Menús de Opciones (Options Menu)** y **Cuadros de Diálogo (Dialog Boxes)**.

## 🚀 Cambios y Mejoras (Paso a Paso)

### 1. Implementación de Menús en XML
Se ha implementado el menú a través de un archivo XML ubicado en `res/menu/options_menu.xml`. Esta es una **buena práctica** en Android, ya que separa la definición visual del menú de la lógica Java/Kotlin.
- **Botón "New Game":** Reinicia el juego actual.
- **Botón "Difficulty":** Despliega un diálogo para elegir la dificultad de la IA.
- **Botón "About":** Muestra información del desarrollador (Reto adicional).
- **Botón "Quit":** Pregunta si estás seguro de salir y cierra la app.

### 2. Modificaciones en la Lógica (`BoardGame.java`)
- Se implementó un *Enumerador* `DifficultyLevel` (Easy, Harder, Expert).
- La IA ahora calcula su movimiento (`getComputerMove()`) dependiendo de la dificultad seleccionada:
  - **Easy:** Selecciona un lugar al azar usando `getRandomMove()`.
  - **Harder:** Intenta ganar (`getWinningMove()`); si no puede, hace un movimiento aleatorio.
  - **Expert:** Intenta ganar; si no puede, bloquea al usuario (`getBlockingMove()`), y si nada funciona, mueve al azar.

### 3. Integración en `MainActivity.java`
- Se enlazó el menú creado utilizando el método sobreescrito `onCreateOptionsMenu(Menu menu)`.
- Se gestionaron los clicks del menú en `onOptionsItemSelected(MenuItem item)`.
- Se emplearon **Alert Dialogs** mediante el método `onCreateDialog(int id)`:
  - Diálogo de Confirmación (Quit): Un cuadro `Yes/No`.
  - Diálogo de Selección Simple (Difficulty): Listado de opciones (Radio buttons) para seleccionar el nivel.
  - Diálogo de Interfaz Personalizada (About): Infla un archivo layout `about_dialog.xml` usando un *LayoutInflater*.

## 📚 Conceptos Claves para Estudiar
1. **Menu Resource (XML):** Cómo los elementos `<menu>` y `<item>` estructuran opciones accesibles.
2. **AlertDialog.Builder:** El patrón de diseño Builder para configurar y construir diálogos sin complicaciones.
3. **LayoutInflater:** La forma de transformar (inflar) un archivo XML (como `about_dialog.xml`) en una vista (View) real para ser manipulada por Java en tiempo de ejecución.
4. **Toast:** Mensajes efímeros para notificar al usuario (ej. cuando se selecciona una dificultad).
5. **Enumeradores (Enums):** Estructuras de datos para agrupar estados fijos de configuración.

## 📲 Instalación y envío por correo
El archivo APK ya fue generado (`app-debug.apk`). 
> ⚠️ **Atención al enviarlo por correo:** Gmail y otros gestores bloquean los archivos `.apk` por seguridad. 
**Solución recomendada:** 
- Cambiar la extensión del archivo a `.txt` antes de adjuntarlo (ej. `app.apk` a `app.txt`) y que quien lo reciba lo renombre de nuevo a `.apk`.
- Subirlo a Google Drive y compartir el enlace.