package com.keycloak.starterpaket.service;


import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.requests.RefreshTokenAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    @Value("${keycloak.resource}")
    private String keycloak_client_id;
    @Value("${token-endpoint}")
    private String token_endpoint;
    @Value("${auth-endpoint}")
    private String auth_endpoint;
    @Value("${keycloak.credentials.secret}")
    private String client_secret;

    @Value("${urls-time-to-live}")
    private int urlsTimeToLive;


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


    private void removeAllUrlsThatAreToOld(){
        AuthUrl.urls.removeAll( AuthUrl.urls.stream().filter(url -> (url.getLocalTime().plusMinutes(urlsTimeToLive).isBefore(LocalTime.now()))).collect(Collectors.toList()));
        System.out.println(AuthUrl.urls);
    }

       public AuthUrl generateAuthUrl(String redirect) throws Exception{
           var authUrl = new AuthUrl();
           authUrl.setVerifier(getCodeVerifier());

               authUrl.setChallenge(getCodeChallenge(authUrl.getVerifier()));

           authUrl.setAccess_code(UUID.randomUUID().toString().replace("-","x"));
           authUrl.setRedirect(redirect);
           authUrl.setUrl(getAuthUrl(authUrl.getChallenge(), authUrl.getAccess_code(), redirect));
           authUrl.setLocalTime(LocalTime.now());
           AuthUrl.urls.add(authUrl);
           removeAllUrlsThatAreToOld();
         /*AuthUrl.urls.removeAll( AuthUrl.urls.stream().filter(url -> (url.getLocalTime().plusMinutes(3).isBefore(LocalTime.now()))).collect(Collectors.toList()));
                 //(((new Date()) -url.getTimestamp() )> 30)).collect(Collectors.toList())
           System.out.println(AuthUrl.urls); */
           return authUrl;
       }

       public AuthUrl findAuthUrl(AuthAccess authAccess){
           AuthUrl auth = AuthUrl.urls.stream().filter(authUrl -> authUrl.getAccess_code().equals(authAccess.getAccess_code()))
                   .findFirst().orElse(null);
           return auth;
       }

       public String keycloakTokenRequest(String code, AuthUrl auth) throws UnirestException {
           HttpResponse<String> response =
                   Unirest.post(token_endpoint)
                           .header("content-type", "application/x-www-form-urlencoded")
                           .body("grant_type=authorization_code&client_id="+keycloak_client_id
                                   +"&code_verifier="+auth.getVerifier()
                                   +"&code="+code
                                   +"&redirect_uri="+auth.getRedirect() // brauche ich doch, sonst fehler // warum?
                                   +"&client_secret="+client_secret
                           )
                           .asString();
           return response.getBody();
       }

    public String keycloakTokenRequestWithRefreshToken(RefreshTokenAccess refreshToken) throws UnirestException {
        HttpResponse<String> response =
                Unirest.post(token_endpoint)
                        .header("content-type", "application/x-www-form-urlencoded")
                        .body("grant_type=refresh_token&client_id="+keycloak_client_id
                                +"&refresh_token="+refreshToken.getRefresh_token()
                               // +"&redirect_uri="+ "http://localhost:3000/*"
                                +"&client_secret="+client_secret
                        )
                        .asString();
        return response.getBody();
    }

    public String keycloakTokenCredentialsRequest() throws UnirestException {
        HttpResponse<String> response =
                Unirest.post(token_endpoint)
                        .header("content-type", "application/x-www-form-urlencoded")
                        .body("grant_type=client_credentials&client_id="+keycloak_client_id
                                +"&redirect_uri="+ "http://localhost:3000/*" // brauche ich nicht? weil in keycloak console
                                +"&client_secret="+client_secret
                        )
                        .asString();
        return response.getBody();

    }
}