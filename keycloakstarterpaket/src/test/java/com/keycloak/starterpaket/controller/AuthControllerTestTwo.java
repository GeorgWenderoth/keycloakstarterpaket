package com.keycloak.starterpaket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.keycloak.starterpaket.KeycloakstarterpaketApplication;
import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.keycloak.starterpaket.service.KeycloakService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.any;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//3-15
@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = KeycloakstarterpaketApplication.class)
@AutoConfigureMockMvc

@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)

public class AuthControllerTestTwo {
    @Autowired
    private MockMvc mvc;

    @Mock
   private KeycloakService keycloakService;

    @InjectMocks
    private AuthController controller;

    @Test
    public void test() throws Exception{

        mvc.perform(get("/api/auth/url?redirect=http://localhost:3000/*")
                .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    public void testUrl() throws Exception{

        mvc.perform(get("/api/auth/url?redirect=http://localhost:3000/*")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_code").isString());

    }
    @Test
    public void testUrl_fail() throws Exception{

        mvc.perform(get("/api/auth/url?redirect=http://localhost:3000/*")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_code").isString());

    }
   /* @TestConfiguration
    static class testImpl {
        @Bean
       public KeycloakService keycloakService(){
            return new KeycloakService(){
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

                public AuthUrl generateAuthUrl(String redirect) throws Exception{
                    var authUrl = new AuthUrl();
                    authUrl.setVerifier(getCodeVerifier());

                    authUrl.setChallenge(getCodeChallenge(authUrl.getVerifier()));

                    authUrl.setAccess_code(UUID.randomUUID().toString().replace("-","x"));
                    authUrl.setRedirect(redirect);
                    authUrl.setUrl(getAuthUrl(authUrl.getChallenge(), authUrl.getAccess_code(), redirect));
                    AuthUrl.urls.add(authUrl);
                    return authUrl;
                }

                public AuthUrl findAuthUrl(AuthAccess authAccess){
                    AuthUrl auth = AuthUrl.urls.stream().filter(authUrl -> authUrl.getAccess_code().equals(authAccess.getAccess_code()))
                            .findFirst().orElse(null);
                    return auth;
                }

                public String keycloakRequest(AuthAccess authAccess, AuthUrl auth) throws UnirestException {
                   /* HttpResponse<String> response =
                            Unirest.post(token_endpoint)
                                    .header("content-type", "application/x-www-form-urlencoded")
                                    .body("grant_type=authorization_code&client_id="+keycloak_client_id
                                            +"&code_verifier="+auth.getVerifier()
                                            +"&code="+authAccess.getCode()
                                            +"&redirect_uri="+auth.getRedirect() // brauche ich nicht? weil in keycloak console
                                            +"&client_secret="+client_secret
                                    )
                                    .asString();
                    return response.getBody(); */
    /*     String body = "{\"access_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTYsImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}";
                    return body;
                }
            };
        }


    }
    */



    @Test
    public void testToken_MockKeycloak() throws Exception{
        //keycloakService.keycloakRequest()
      MvcResult test =  mvc.perform(get("/api/auth/url?redirect=http://localhost:3000/*")
              .contentType(MediaType.APPLICATION_JSON))
              .andReturn();



       String a = test.getResponse().getContentAsString();



        Mockito.when(keycloakService.keycloakRequest("idajowdijdaiwoj", new AuthUrl() )).thenReturn("{\"access_token\":\"eyJhbGciOiJSUzI1" +
                "NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTY" +
                "sImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW" +
                "1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F" +
                "21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c" +
                "05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}");

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.readValue(a, Map.class);
        String accesscode = (String) map.get("access_code");


      /*  ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.readValue(response.getBody(), Map.class);
        String accesscode = (String) map.get("access_code"); */
       // test ="/api/auth/url?redirect=http://localhost:3000/*"

       // ob =

        AuthAccess authAccess = new AuthAccess("idajowdijdaiwoj", accesscode);
        var response = controller.getToken(authAccess);
            assertEquals( "{\"access_token\":\"eyJhbGciOiJSUzI1" +
                    "NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTY" +
                    "sImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW" +
                    "1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F" +
                    "21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c" +
                    "05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}", response.getBody());

        /*mvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON).content( "{\"code\": \"idajowdijdaiwoj\", \"access_code\":\"" + accesscode + "\"}"
         ))
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(status().isOk());
 */
    }



  @Test
  public void mockControllerUrlTest() throws Exception {


        AuthUrl url = new AuthUrl();
        url.setUrl("url");
        Mockito.when(keycloakService.generateAuthUrl("http://localhost:3000/*")).thenReturn(url);


        var response = controller.getAuthUrl("http://localhost:3000/*");
        assertEquals(url, response.getBody());
  }

  @Test
    public void mockControllerToken () {

        AuthAccess access = new AuthAccess();
        access.setAccess_code("abc");
        access.setCode("123");
        AuthUrl url = new AuthUrl();
        url.setUrl("url");

        Mockito.when(keycloakService.findAuthUrl(access)).thenReturn(url);
        try {
            Mockito.when(keycloakService.keycloakRequest(access.getCode(), url)).thenReturn("token");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }




}
