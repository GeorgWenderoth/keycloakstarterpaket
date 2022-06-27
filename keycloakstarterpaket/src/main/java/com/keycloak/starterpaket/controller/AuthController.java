package com.keycloak.starterpaket.controller;

import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.requests.RefreshTokenAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.keycloak.starterpaket.service.KeycloakService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;

    @GetMapping("url")
    public ResponseEntity<AuthUrl> getAuthUrl(@RequestParam String redirect ){
        try {
            var authUrl=  keycloakService.generateAuthUrl(redirect);
            return ResponseEntity.ok(authUrl);
        }catch (Exception exception){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("token")
    public ResponseEntity<?> getToken(@RequestBody AuthAccess authAccess) throws UnirestException {
        AuthUrl auth = keycloakService.findAuthUrl(authAccess);
        if(auth == null){
            return ResponseEntity.notFound().build();
        }
       String body = keycloakService.keycloakTokenRequest(authAccess.getAuthorization_code(), auth);
        keycloakService.removeAuthUrlFromUrls(auth);
        return ResponseEntity.ok(body);
    }

    @PostMapping("tokenRefreshToken")
    public ResponseEntity<?> getTokenWithRefreshtoken(@RequestBody RefreshTokenAccess refreshToken) throws UnirestException {
        String body = keycloakService.keycloakTokenRequestWithRefreshToken(refreshToken);
        return ResponseEntity.ok(body);
    }

    @PostMapping("tokenCredentials")
    public ResponseEntity<?> getTokenViaCredentialsAndSecret() throws UnirestException {
        String body = keycloakService.keycloakTokenCredentialsRequest();
        return ResponseEntity.ok(body);
    }
}
