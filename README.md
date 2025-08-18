# http-server-arep

Servidor HTTP mínimo en Java — clase principal: `escuelaing.edu.co.HttpServer` (`src/main/java/escuelaing/edu/co/HttpServer.java`). Puerto por defecto: `35000`.

## Requisitos

* JDK 17 instalado; `JAVA_HOME` apuntando al JDK.
* Maven 3.x.
* Ejecutar desde la raíz del proyecto (donde está `pom.xml`).

## Ejecución desde IDE (NetBeans / IntelliJ)

Asegura JDK 17 y Maven configurados en el proyecto.

### NetBeans

![NetBeans configuración](src/main/resources/images/img_9.png)

### IntelliJ IDEA

![IntelliJ configuración 1](src/main/resources/images/img_10.png)

![IntelliJ configuración 2](src/main/resources/images/img_11.png)

Para ejecutar: usa la opción Run/Execute del IDE.

## Ejecución (PowerShell)

```powershell
# Instalar dependencias (desde la raíz del proyecto)
mvn install

# Limpiar y construir el JAR
mvn clean package

# Ejecutar el servidor
java -jar target/http-server-arep-1.0-SNAPSHOT.jar
```

Imágenes de ejemplo (ejecución):

![mvn install](src/main/resources/images/img_6.png)
![mvn package](src/main/resources/images/img_7.png)
![java -jar](src/main/resources/images/img_8.png)

**Detener el servidor:** `GET /stop` o `Ctrl+C`.

## Arquitectura (resumen)

* **Entrada:** `ServerSocket` en puerto `35000`.
* **Routing:** se extrae la ruta con `uri.split("?")[0]` y se hace `switch` por path.
* **Recursos:** archivos en `src/main/resources` (usar `getResourceAsStream` para que funcione empaquetado en JAR).
* **Estado:** `List<String> box` en memoria para `/books` — no persistente.
* **Detener:** ruta `http://localhost:35000/stop` cierra el `ServerSocket`.
* **Limitaciones:** sin pool de hilos, parsing HTTP minimalista, sin validación de entradas; solo maneja una solicitud a la vez.

## Evaluación / Pruebas (plantilla)

* **Objetivo:**
* **Pasos:**
* **Entrada:**
* **Resultado esperado:**
* **Resultado obtenido:**
* **Comentarios:**

## TODO

Adjuntar pruebas realizadas y habilitar rutas para servir las imágenes del README si se desea mostrarlas desde el JAR.
