package escuelaing.edu.co;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple HTTP server that handles basic requests and serves static files.
 * @author Miguel Angel Motta
 * @version 1.0
 * @since 2025-08-15
 */
public class HttpServer {
    private static final Logger logger = Logger.getLogger(HttpServer.class.getName());
    private static boolean running = true;
    private static final List<String> box = new ArrayList<>();

    /**
     * Main method to start the HTTP server.
     * The server listens on port 35000 and handles incoming requests. It won't stop until the "/stop" route is accessed.
     * @param args command line arguments (not used)
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public static void main(String[] args) throws IOException {


        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
            logger.info("Running Server... on port 35000");
        } catch (IOException e) {
            logger.warning("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        int i = 0;
        while (running){
            i++;
            logger.info("Number of connections: " + i);
            clientSocket = acceptClient(serverSocket);
            handleRequest(clientSocket);
        }
        serverSocket.close();
    }

    /**
     * Prints the trace of the input stream from the client.
     * This method reads lines from the input stream and logs them until no more data is available.
     * @param in the BufferedReader to read from
     * @throws IOException if an I/O error occurs when reading from the input stream
     */
    public static void printTrace(BufferedReader in) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            logger.info("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }
    }

    /**
     * Accepts a client connection and returns the connected socket.
     * @param serverSocket the server socket to accept connections from
     * @return the socket connected to the client
     * @throws IOException if an I/O error occurs when accepting the connection
     */
    public static Socket acceptClient(ServerSocket serverSocket) throws IOException {
        logger.info("Waiting for a client connection...");
        Socket clientSocket;
        clientSocket = serverSocket.accept();
        logger.info("New connection accepted");
        return clientSocket;
    }

    /**
     * Handles the incoming request from the client.
     * It reads the request line, validates it, and processes the request based on the URI.
     * @param clientSocket the socket connected to the client
     * @throws IOException if an I/O error occurs when reading from or writing to the socket
     */
    public static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
        String line = in.readLine();
        if (line == null || line.split(" ").length <= 1) {
            logger.warning("Invalid request received");
            sendResponse(new PrintWriter(clientSocket.getOutputStream(), true), "400 Bad Request");
            clientSocket.close();
            return;
        }
        String uri = line.split(" ")[1];
        handleValidRoute(uri, clientSocket);
        in.close();
        clientSocket.close();
    }


    /**
     * Handles valid routes based on the URI.
     * It serves static files or processes specific requests like "/stop", "/name", and "/books".
     * @param uri the requested URI
     * @param clientSocket the socket connected to the client
     * @throws IOException if an I/O error occurs when reading from or writing to the socket
     *
     * TODO - Improve the request handling to support more complex routes, different methods and parameters.
     */
    public static void handleValidRoute(String uri, Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedOutputStream outData = new BufferedOutputStream(clientSocket.getOutputStream());
        String path = uri.split("\\?")[0];
        switch (path) {
            case "/", "/index.html":
                sendFileResponse(out, outData, "/index.html", "text/html");
                break;
            case "/favicon.ico":
                sendFileResponse(out, outData, "/favicon.ico", "image/x-icon");
                break;
            case "/styles.css":
                sendFileResponse(out, outData, "/styles.css", "text/css");
                break;
            case "/script.js":
                sendFileResponse(out, outData, "/script.js", "application/javascript");
                break;
            case "/stop":
                stopServer();
                sendResponse(out, "Server is stopping...");
                break;
            case "/name":
                sendResponse(out, "Your name is: GUEST USER");
                break;
            case "/about":
                sendFileResponse(out, outData, "/about/about.html", "text/html");
                break;
            case "/about/styles.css":
                sendFileResponse(out, outData, "/about/styles.css", "text/css");
                break;
            case "/about/script.js":
                sendFileResponse(out, outData, "/about/script.js", "application/javascript");
                break;
            case "/books":
                if (uri.split("\\?").length > 1 && uri.split("\\?")[1].startsWith("name=")) {
                    String bookName = uri.split("\\?")[1].split("=")[1];
                    saveData(bookName);
                }
                sendJSONResponse(out, "Books saved: " + box.toString());
                break;

            default:
                sendResponse(out, "404 Not Found");
        }
        outData.close();
    }

    /**
     * Sends a file response to the client.
     * It reads the file from the resources directory and sends it back with the appropriate content type.
     * @param out the PrintWriter to write the response
     * @param outData the BufferedOutputStream to write the file data
     * @param filePath the path of the file to be sent
     * @param contentType the content type of the file
     */
    public static void sendFileResponse(PrintWriter out, BufferedOutputStream outData, String filePath, String contentType) {
        try {
            File file = new File("src/main/resources" + filePath);
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fileIn = new FileInputStream(file);
            fileIn.read(fileData);
            fileIn.close();

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + fileData.length);
            out.println();
            out.close();

            outData.write(fileData, 0, fileData.length);
        } catch (IOException e) {
            logger.warning("File not found: " + filePath);
            sendResponse(out, "404 Not Found");
        }
    }

    /**
     * Saves the data received from the client.
     * It decodes the value and adds it to the box list, logging the action.
     * @param value the value to be saved
     */
    public static void saveData(String value) {
        String parsedData = URLDecoder.decode(value, StandardCharsets.UTF_8);
        box.add(parsedData);
        logger.info("Data saved: " + parsedData);
    }

    /**
     * Sends a JSON response to the client.
     * It constructs a JSON string and sends it back with the appropriate content type.
     * @param out the PrintWriter to write the response
     * @param jsonContent the JSON content to be sent
     */
    public static void sendJSONResponse(PrintWriter out, String jsonContent) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonContent.length());
        out.println();
        out.print(jsonContent);
    }

    /**
     * Sends a plain text response to the client.
     * It constructs a response string and sends it back with the appropriate content type.
     * @param out the PrintWriter to write the response
     * @param content the content to be sent
     */
    public static void sendResponse(PrintWriter out, String content) {
        out.println("HTTP/1.1 OK");
        out.println("Content-Type: text/plain");
        out.println("Content-Length: " + content.length());
        out.println();
        out.print(content);
    }

    /**
     * Stops the server by setting the running flag to false.
     * This will cause the main loop to exit and the server to shut down gracefully.
     */
    public static void stopServer() {
        running = false;
        logger.info("Server is stopping...");
    }


}
