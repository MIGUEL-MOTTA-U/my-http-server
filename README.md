# http-server-arep

Servidor HTTP mínimo en Java (clase principal [`escuelaing.edu.co.HttpServer`](src/main/java/escuelaing/edu/co/HttpServer.java)) que sirve archivos estáticos y gestiona una ruta `/books` para guardar nombres en memoria. Puerto por defecto: `35000`.

## Requisitos

* JDK 17 instalado y `JAVA_HOME` apuntando al JDK.
* Maven 3.x.
* Ejecutar desde la raíz del proyecto (donde está [`pom.xml`](pom.xml)).

## Instalación y ejecución (PowerShell)

```powershell
# compilar
mvn compile

# ejecutar en desarrollo (plugin exec)
mvn -Dexec.mainClass="escuelaing.edu.co.HttpServer" org.codehaus.mojo:exec-maven-plugin:3.1.0:java

# empaquetar
mvn package

# ejecutar el JAR generado
java -jar target/http-server-arep-1.0-SNAPSHOT.jar
```

Parar: `GET /stop` o `Ctrl+C`.

## Arquitectura (resumen)

* Entrada: `ServerSocket` en puerto 35000.
* Routing: extracción de la ruta con `uri.split("\?")[0]` y `switch` por path.
* Recursos: servir archivos desde `src/main/resources`.
* Estado: `List<String> box` en memoria para `/books` (no persistente porque lo alojo en memoria, es decir, mientras corre la aplicación).
* Detener: `ServerSocket.close()` en `/stop` (para detenerlo se consulta la ruta [`localhost:35000/stop`](http://localhost:35000/stop)).
* Limitaciones: sin pool de hilos, parsing HTTP mínimo, sin seguridad ni validación (tampoco es concurrente por lo que solo maneja una solicitud a la vez).

## Evaluación / Pruebas (plantilla)

* **PRUEBAS:**

    * **Objetivo:**
    * **Pasos:**
    * **Entrada:**
    * **Resultado esperado:**
    * **Resultado obtenido:**
    * **Comentarios:**

## TODO --> Anexar las pruebas realizadas y habilitar rutas para obtener imágenes.
