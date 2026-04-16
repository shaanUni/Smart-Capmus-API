package com.mycompany.smart.campus.api;

import com.mycompany.smart.campus.api.config.AppConfig;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class SmartCampusApi {

    private static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static HttpServer startServer() {
        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI),
                new AppConfig()
        );
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();
        System.out.println("Smart Campus API running at " + BASE_URI);
        System.out.println("Try:");
        System.out.println("  GET " + BASE_URI);
        System.out.println("  GET " + BASE_URI + "rooms");
        System.out.println("Press ENTER to stop the server...");
        System.in.read();
        server.shutdownNow();
    }
}