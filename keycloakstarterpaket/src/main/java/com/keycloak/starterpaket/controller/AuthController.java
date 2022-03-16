package com.keycloak.starterpaket.controller;

import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.keycloak.starterpaket.service.KeycloakService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;



@RestController
@CrossOrigin("*")
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    /*
    @Value("${keycloak.resource}")
    private String keycloak_client_id;
    @Value("${token-endpoint}")
    private String token_endpoint;
    @Value("${auth-endpoint}")
    private String auth_endpoint;
    @Value("${keycloak.credentials.secret}")
    private String client_secret;


    private String getCodeVerifier(){
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    private String getCodeChallenge(String verifier) throws Exception{
        byte[] bytes = verifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(digest);
    }

    private String getAuthUrl(String challange, String state, String redirect){
        return auth_endpoint+ "?" +
                "response_type=code&" +
                "code_challenge=" + challange + "&" +
                "code_challenge_method=S256&" +
                "client_id=" + keycloak_client_id + "&" +
                "redirect_uri="+ redirect +"&" +
                "state="+ state +"";
    }
    */

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
       String body = keycloakService.keycloakRequest(authAccess.getCode(), auth);
        AuthUrl.urls.remove(auth);
        return ResponseEntity.ok(body);
    }
}

/*
@PostMapping("token")
    public ResponseEntity<?> getToken(@RequestBody AuthAccess authAccess) throws UnirestException {
        AuthUrl auth = AuthUrl.urls.stream().filter(authUrl -> authUrl.getAccess_code().equals(authAccess.getAccess_code()))
                .findFirst().orElse(null);
        if(auth == null){
            return ResponseEntity.notFound().build();
        }
        HttpResponse<String> response =
                Unirest.post(token_endpoint)
                        .header("content-type", "application/x-www-form-urlencoded")
                        .body("grant_type=authorization_code&client_id="+keycloak_client_id
                                +"&code_verifier="+auth.getVerifier()
                                +"&code="+authAccess.getCode()
                                +"&redirect_uri="+auth.getRedirect() // brauche ich nicht? weil in keycloak console
                                +"&client_secret="+client_secret
                        )
                        .asString();
        AuthUrl.urls.remove(auth);
        return ResponseEntity.ok(response.getBody());
    }
 */
