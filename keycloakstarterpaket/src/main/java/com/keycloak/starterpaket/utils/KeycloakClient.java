package com.keycloak.starterpaket.utils;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KeycloakClient {
    @Value("${keycloak.auth-server-url}")
    private String apiUri;
    private String keycloakApi;
    @Value("${keycloak.resource}")
    protected String keycloakClientId;
    @Value("${keycloak.credentials.secret}")
    protected String keycloakSecret;

    protected ResponseEntity<?> getResponseEntityFromKeycloakOpenIdToken(String[] body, String path) {
        keycloakApi = apiUri + path;
        try {
            StringBuilder requestBody = new StringBuilder();
            for(int i = 0; i<body.length; i++){
                requestBody.append(body[i]);
                if(i%2==0){
                    requestBody.append("=");
                }else if(i != body.length-1){
                    requestBody.append("&");
                }
            }
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(keycloakApi))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody.toString()
                            )
                    ).build();
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            var json = new JSONParser(response.body());
            return ResponseEntity.ok(json.object());
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    protected String getAdminToken(String[] body, String path) {
        keycloakApi = apiUri + path;
        try {
            StringBuilder requestBody = new StringBuilder();
            for(int i = 0; i<body.length; i++){
                requestBody.append(body[i]);
                if(i%2==0){
                    requestBody.append("=");
                }else if(i != body.length-1){
                    requestBody.append("&");
                }
            }
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(keycloakApi))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody.toString()
                            )
                    ).build();
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            var json = new JSONParser(response.body());
            return response.body();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    protected String registerNewUser(String[] body, String path, String token) {
        keycloakApi = apiUri + path;
        try {
            StringBuilder requestBody = new StringBuilder();
            for(int i = 0; i<body.length; i++){
                requestBody.append(body[i]);
                if(i%2==0){
                    requestBody.append(":");
                }else if(i != body.length-1){
                    requestBody.append(",");
                }
            }
            //var jsonEins = new JSONParser(requestBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(keycloakApi))
                    .headers("Content-Type", "application/json", "Authorization", "bearer ")
                    .POST(

                           HttpRequest.BodyPublishers.ofString(
                                    requestBody.toString()
                            )
                    ).build();
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            var json = new JSONParser(response.body());
            return response.body();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}

