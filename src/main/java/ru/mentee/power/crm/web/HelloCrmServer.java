package ru.mentee.power.crm.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HelloCrmServer {

    private final HttpServer server;

    public HelloCrmServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void start() {
        server.createContext("/hello", new HelloHandler());

        server.start();
        System.out.println("Server started on http://localhost:" + server.getAddress().getPort());
    }

    public void stop() {
        server.stop(0);
    }

    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            System.out.println("Received " + method + " request for " + path);

            String response = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <title>Hello</title>
                </head>
                <body>
                    <h1>Hello CRM!</h1>
                </body>
                </html>
                """;

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}