package com.keycloak.starterpaket.controller;


import com.keycloak.starterpaket.requests.KeycloakUser;
import com.keycloak.starterpaket.utils.KeycloakClient;
import com.keycloak.starterpaket.utils.AdminTokenClient;
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

    String a = "/realms/SpringbootKeycloak/protocol/openid-connect/token";
    String b = "/realms/master/protocol/openid-connect/token";
    String userpath = "/admin/realms/SpringbootKeycloak/users";

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody String refresh_token){
        String[] body = {
                "grant_type" , "refresh_token",
                "client_id", keycloakClientId,
                "client_secret", keycloakSecret,
                "refresh_token", refresh_token
        };
        return getResponseEntityFromKeycloakOpenIdToken(body, a);

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
        return getResponseEntityFromKeycloakOpenIdToken(body, a);
    }

    @PostMapping("admintoken")
    public ResponseEntity<?> admintoken(@RequestBody KeycloakUser user) {
        String[] body = {
                "grant_type" , "password",
                "client_id", "admin-cli",

                "username", "georg",
                "password", "12345678"
        };
        return getResponseEntityFromKeycloakOpenIdToken(body, b);
    }


    @PostMapping("register")
    public String register(@RequestBody KeycloakUser user) {
        String[] tokenbody = {
                "grant_type" , "password",
                "client_id", "admin-cli",

                "username", "georg",
                "password", "12345678"
        };


        String[] body = {


                "username", user.getUsername(),

        };
        String test = getAdminToken(tokenbody, b);

        String t = registerNewUser(body, userpath, test);
        return t;

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
