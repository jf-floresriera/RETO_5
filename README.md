# Reto 5 - Tres en Raya (Gráficos y Sonidos) 🎮🔊

Bienvenido a la solución del **Reto 5**. Este repositorio es una evolución de los retos anteriores, y se enfoca en enseñar conceptos avanzados del desarrollo de interfaces en Android: **Gráficos Personalizados (Custom Views)** y **Reproducción de Medios (Media Player)**.

Este documento está diseñado como una **guía de estudio** detallada, ideal para un estudiante que está aprendiendo a desarrollar en Android.

---

## 🏗️ Arquitectura y Novedades del Reto 5

En el Reto 4 utilizábamos un `GridLayout` que contenía 9 `Buttons` distintos para armar el tablero. Aunque funciona, no es escalable ni eficiente a nivel de memoria y gráficos. En este Reto 5 hemos migrado la arquitectura visual a una **Custom View (`BoardView.java`)**.

### 1. Gráficos Personalizados: `BoardView.java`
Hemos creado una clase que hereda de `android.view.View`. Esto nos otorga control total sobre cada píxel que se dibuja en pantalla.
*   **El método `onDraw(Canvas canvas)`:** En lugar de dejar que el sistema operativo dibuje botones, nosotros interceptamos este método para dibujar manualmente con un `Canvas` y un `Paint`:
    *   Trazamos 4 líneas (2 verticales y 2 horizontales) utilizando `canvas.drawLine()` para formar la grilla o "gato".
    *   Calculamos dinámicamente las dimensiones: usamos `getWidth()` y `getHeight()` para dividir la vista exactamente en 3 partes. ¡Esto hace que la interfaz sea **100% responsiva** sin importar si estás en un celular pequeño o en una tablet!
*   **Imágenes (Bitmaps y VectorDrawables):**
    *   El tutorial original sugería usar imágenes PNG y cargarlas con `BitmapFactory.decodeResource`. Como expertos, fuimos un paso más allá y creamos **Vector Drawables (XML)** (`x_img.xml` y `o_img.xml`), los cuales no pierden calidad al redimensionarse.
    *   Para seguir los lineamientos didácticos de dibujar con `canvas.drawBitmap()`, creamos un método que convierte esos vectores SVG/XML a `Bitmap` al instante mediante `Canvas` y `Drawable.draw()`. Luego, usamos los cálculos matemáticos (con márgenes de padding) para colocar la X o la O perfectamente en su celda usando un `Rect`.

### 2. Detección de toques con `OnTouchListener`
Al remover los botones, ya no podemos usar `setOnClickListener`.
*   Implementamos un `View.OnTouchListener` directamente en `MainActivity`.
*   Capturamos las coordenadas del toque con `event.getX()` y `event.getY()`.
*   Mediante una división matemática (`event.getX() / widthCell`) sabemos exactamente en qué columna y fila (de la celda 0 a la 8) tocó el usuario el tablero, permitiendo a la IA jugar en consecuencia.
*   Para actualizar los gráficos tras cada jugada, llamamos al método `mBoardView.invalidate()`, el cual notifica a Android que debe volver a ejecutar `onDraw()`.

### 3. Sonidos con `MediaPlayer`
Un juego está incompleto sin respuesta auditiva.
*   Añadimos la carpeta `res/raw` para almacenar archivos binarios (`.wav`), los cuales se cargan a través del identificador `R.raw.sword` y `R.raw.swish`.
*   **Manejo del Ciclo de Vida (Lifecycle):** Es vital para un estudiante comprender que los recursos multimedia consumen mucha memoria ram y batería.
    *   Instanciamos y preparamos los sonidos en `onResume()` (`MediaPlayer.create(...)`).
    *   **Muy importante:** Liberamos la memoria (`release()`) en `onPause()`. Si el jugador recibe una llamada telefónica o minimiza la app, no dejaremos recursos de audio atrapados (Memory Leaks).
*   En cada jugada local o de computadora, reproducimos el sonido llamando simplemente a `mMediaPlayer.start()`.

### 4. Extra Challenge (Delay / Retraso de la computadora)
El tutorial solicita como reto adicional que la computadora espere (aprox. 1 segundo) antes de jugar, ya que de otro modo el movimiento es instantáneo y arruina la ilusión del sonido y del mensaje "Turno de la IA".
*   💡 **Nota al estudiante:** Este desafío *ya lo teníamos resuelto* desde el Reto 3/4. No usamos un ciclo bloqueante (como un `Thread.sleep`), porque eso congelaría el "UI Thread" (hilo de interfaz) causando un error tipo ANR (App Not Responding).
*   En su lugar, usamos `new Handler(Looper.getMainLooper()).postDelayed(Runnable, 500)`, el cual encola la instrucción y deja que el tablero termine de pintarse, y medio segundo después, invoca a la IA.

---

## 🛠️ Resumen Visual de los Archivos Clave
*   `BoardView.java`: Donde ocurre la magia del `Canvas` y `Paint`.
*   `MainActivity.java`: Controlador que coordina la View, el juego (`BoardGame.java`) y los sonidos.
*   `activity_main.xml`: Aquí cambiamos todo el bloque de botones gigantes por un único elemento `<com.example.tictactoe.basic.BoardView>`.
*   `res/raw/`: Directorio donde residen los `.wav`.
*   `res/drawable/`: Directorio donde hicimos las imágenes vectoriales.

---

## 📲 APK para instalación directa
La aplicación compilada y lista para probar en tu Samsung se encuentra adjunta en este repositorio bajo la carpeta `APK/`. El archivo se llama `TresEnRaya_Reto5.apk`. (En caso de tener problemas al descargar desde ciertos navegadores, pulsa sobre el archivo en GitHub y haz click en el botón "*Download raw file*").