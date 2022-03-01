package com.keycloak.starterpaket.controller;


import com.keycloak.starterpaket.requests.KeycloakUser;
import com.keycloak.starterpaket.utils.KeycloakClient;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@CrossOrigin("*")
@RequestMapping("api/auth/")
public class AuthController extends KeycloakClient {

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody String refresh_token){
        String[] body = {
                "grant_type" , "refresh_token",
                "client_id", keycloakClientId,
                "client_secret", keycloakSecret,
                "refresh_token", refresh_token
        };
        return getResponseEntityFromKeycloakOpenIdToken(body, "/realms/beispiel/protocol/openid-connect/token");

    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody KeycloakUser user) {
        String[] body = {
                "grant_type" , "password",
                "client_id", keycloakClientId,
                "client_secret", keycloakSecret,
                "username", user.getUsername(),
                "password", user.getPassword()
        };
        return getResponseEntityFromKeycloakOpenIdToken(body, "/realms/beispiel/protocol/openid-connect/token");
    }

    @RolesAllowed({"user", "admin"})
    @GetMapping("userinfo")
    public ResponseEntity<?> userinfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        KeycloakPrincipal principal = (KeycloakPrincipal) auth.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken token = session.getToken();
        String id = token.getId();
        return ResponseEntity.ok(id);
    }
}
