package com.keycloak.starterpaket.controller;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class TestController {

    @RolesAllowed({"user"})
    @GetMapping("/test/user")
    public ResponseEntity<?> testUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (KeycloakPrincipal) auth.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken token = session.getToken();
        String email = token.getEmail();
        return ResponseEntity.ok(Map.of("msg", "Deine angelegte Email ist: " + email));
    }
}