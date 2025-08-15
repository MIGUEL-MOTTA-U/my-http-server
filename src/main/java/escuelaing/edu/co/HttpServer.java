package escuelaing.edu.co;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HttpServer {

    private static final Logger logger = Logger.getLogger(HttpServer.class.getName());
    private static boolean running = true;
    private static final List<String> box = new ArrayList<>();
    public static void main(String[] args) throws IOException {


        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
            System.out.println("Running server");
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

    private static void printTrace(BufferedReader in) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            logger.info("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }
    }

    private static Socket acceptClient(ServerSocket serverSocket) throws IOException {
        logger.info("Waiting for a client connection...");
        Socket clientSocket;
        clientSocket = serverSocket.accept();
        logger.info("New connection accepted");
        return clientSocket;
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
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

    private static void handleValidRoute(String uri, Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedOutputStream outData = null;
        String path = uri.split("\\?")[0];
        switch (path) {
            case "/", "/index.html":
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
                sendFileResponse(out, outData, "/index.html", "text/html");
                break;
            case "/favicon.ico":
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
                sendFileResponse(out, outData, "/favicon.ico", "image/x-icon");
                break;
            case "/styles.css":
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
                sendFileResponse(out, outData, "/styles.css", "text/css");
                break;
            case "/script.js":
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
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
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
                sendFileResponse(out, outData, "/about/about.html", "text/html");
                break;
                case "/about/styles.css":
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
                sendFileResponse(out, outData, "/about/styles.css", "text/css");
                break;
            case "/about/script.js":
                outData = new BufferedOutputStream(clientSocket.getOutputStream());
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
    }

    private static void sendFileResponse(PrintWriter out, BufferedOutputStream outData, String filePath, String contentType) {
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
            out.flush();

            outData.write(fileData, 0, fileData.length);
            outData.flush();
        } catch (IOException e) {
            logger.warning("File not found: " + filePath);
            sendResponse(out, "404 Not Found");
        }
    }

    private static void saveData(String value) {
        String parsedData = URLDecoder.decode(value, StandardCharsets.UTF_8);
        box.add(parsedData);
        logger.info("Data saved: " + parsedData);
    }

    private static void sendJSONResponse(PrintWriter out, String jsonContent) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonContent.length());
        out.println();
        out.print(jsonContent);
        out.flush();
    }

    private static void sendResponse(PrintWriter out, String content) {
        out.println("HTTP/1.1 OK");
        out.println("Content-Type: text/plain");
        out.println("Content-Length: " + content.length());
        out.println();
        out.print(content);
        out.flush();
    }

    private static void stopServer() {
        running = false;
        logger.info("Server is stopping...");
    }


}
