package com.keycloak.starterpaket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keycloak.starterpaket.KeycloakstarterpaketApplication;
import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.requests.RefreshTokenAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.keycloak.starterpaket.service.KeycloakService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//3-15
@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = KeycloakstarterpaketApplication.class)
@AutoConfigureMockMvc

@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)

public class AuthControllerTest {
    @Autowired
    private MockMvc mvc;

    private KeycloakService keycloakService = mock(KeycloakService.class);

    private AuthController controller = new AuthController(keycloakService);


    @Value("${keycloak.resource}")
    private String keycloak_client_id;
    @Value("${token-endpoint}")
    private String token_endpoint;
    @Value("${auth-endpoint}")
    private String auth_endpoint;
    @Value("${keycloak.credentials.secret}")
    private String client_secret;

    @Value("${test-password}")
    private String testpassword;

    @Value("${test-username}")
    private String testusername;

    @Value("${test-redirect-url}")
    private String testredirecturl;

    @Value("${test-user-api}")
    private String testuserapi;
    @Value("${test-refreshtoken-api}")
    private String testrefreshtokenapi;
    @Value("${test-full-redirect-url}")
    private String testfullredirecturl;

    @Test
    public void test() throws Exception {

        mvc.perform(get(testfullredirecturl)
                .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    public void test_Api_Url() throws Exception {

        mvc.perform(get(testfullredirecturl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_code").isString());

    }

   /* @Test
    public void testToken_MockKeycloak() throws Exception {
        //keycloakService.keycloakRequest()
        MvcResult test = mvc.perform(get(testfullredirecturl)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String a = test.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(a, Map.class);
        String accesscode = (String) map.get("access_code");
        AuthAccess authAccess = new AuthAccess("idajowdijdaiwoj", accesscode);
        AuthUrl authUrl = new AuthUrl();
        authUrl.setAccess_code(accesscode);
        AuthUrl.urls.add(authUrl);

        System.out.println("log:" + authUrl.toString());
        Mockito.when(keycloakService.findAuthUrl(authAccess)).thenReturn(authUrl);

        Mockito.when(keycloakService.keycloakRequest("idajowdijdaiwoj", authUrl)).thenReturn("{\"access_token\":\"eyJhbGciOiJSUzI1" +
                "NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTY" +
                "sImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW" +
                "1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F" +
                "21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c" +
                "05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}");


        var response = controller.getToken(authAccess);
        assertEquals("{\"access_token\":\"eyJhbGciOiJSUzI1" +
                "NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTY" +
                "sImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW" +
                "1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F" +
                "21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c" +
                "05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}", response.getBody());
    }
   */

    @Test
    public void test_Controller_getToken_with_UrlAPI_Mock_keycloakRequest() throws Exception {
        //keycloakService.keycloakRequest()
        MvcResult test = mvc.perform(get(testfullredirecturl)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String a = test.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(a, Map.class);
        String accesscode = (String) map.get("access_code");
        AuthAccess authAccess = new AuthAccess("idajowdijdaiwoj", accesscode);
        AuthUrl authUrl = new AuthUrl();
        authUrl.setAccess_code(accesscode);
        AuthUrl.urls.add(authUrl);

        System.out.println("log:" + authUrl.toString());
        System.out.println(authAccess);
        Mockito.when(keycloakService.findAuthUrl(authAccess)).thenCallRealMethod();

        Mockito.when(keycloakService.keycloakTokenRequest("idajowdijdaiwoj", authUrl)).thenReturn("{\"access_token\":\"eyJhbGciOiJSUzI1" +
                "NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTY" +
                "sImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW" +
                "1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F" +
                "21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c" +
                "05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}");

        Mockito.when(controller.getToken(authAccess)).thenCallRealMethod();
        //AuthAccess authAccess = new AuthAccess("idajowdijdaiwoj", accesscode);
        var response = controller.getToken(authAccess);
        assertEquals("{\"access_token\":\"eyJhbGciOiJSUzI1" +
                "NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlR2tjanE3Y3lQcElqeTRJazZ3TFVMaFgxSmc5cjdTcVdSSEJDSVB6QXYwIn0.eyJleHAiOjE2NDcyNjYyMTY" +
                "sImlhdCI6MTY0NzI2NTg1NiwiYXV0aF90aW1lIjoxNjQ3MjY1ODE3LCJqdGkiOiJjMGYyMzJkMC0zZWMzLTQ5YjQtYWY1Yi1kNzFiMWE5OTYyYWEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2ZjAwM2U2ZC1kMzRjLTRjM2QtYjI2MS1hYmEzNjg2ZTUwMGQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZ2Jvb3RrZXljbG9hayIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW" +
                "1lIjoiaGVpZGkga2x1bSIsInByZWZlcnJlZF91c2VybmFtZSI6ImhlaWRpIiwiZ2l2ZW5fbmFtZSI6ImhlaWRpIiwiZmFtaWx5X25hbWUiOiJrbHVtIiwiZW1haWwiOiJoZWlkaUBrbHVtLmRlIn0.g7nIzz48dCVhPLqJdA7EM3M-GAkxgOljP731xrrCimFe4cJaYV_Q9fOPJyCJsP1Mgbn2XeswLFz73Zwx_RF3RNcThUSRA2raQcUQUilDdiXD3h_YP4nnlMFc40Z5Vo5sTIohRgNe2JtAVNTUsE3VpvqobICww9PQPvA12jiVgwnKXN2f62WRMNKQYT_3LvXGTxdI4QwRE_PmUyS2STDx-vH83FcAYOuxvjpETkXJRMSgy3w85lFMdq4VCFFAcUUK8tPviA8DWveyVryeXdnRpanvvhtuo90PYfqbA1MRiZtV68HXEmKxq7pSzH9wlMSTN4GErugu1pGaXaQ3v6GwhQ\",\"expires_in\":360,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M2ViYjFkYi1mZWFiLTQxZDEtYjhiNS0wMzFlMTdmYTNhZGQifQ.eyJleHAiOjE2NDcyNjc2NTYsImlhdCI6MTY0NzI2NTg1NiwianRpIjoiYTBmNTllYjAtZGE0MS00NjAzLWJiZWItNTNhYjU3OTRkM2Q4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdib290S2V5Y2xvYWsiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1NwcmluZ2Jvb3RLZXljbG9hayIsInN1YiI6IjZmMDAzZTZkLWQzNGMtNGMzZC1iMjYxLWFiYTM2ODZlNTAwZCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNWY0NDRjMDUtNjhjZC00ZGU2LTk5ZjMtOTczZGNhNGY1MWQwIn0.irNyresT9wUo0F" +
                "21pKwSN5xL9BagZr4PXkBaD7Dmvnk\",\"token_type\":\"Bearer\",\"not-before-policy\":0,\"session_state\":\"5f444c" +
                "05-68cd-4de6-99f3-973dca4f51d0\",\"scope\":\"profile email\"}", response.getBody());
    }

    @Test
    public void Test_Controller_getAuthUrl_Mock_KeycloakService_generateAuthUrl() throws Exception {


        AuthUrl url = new AuthUrl();
        url.setUrl("url");
        Mockito.when(keycloakService.generateAuthUrl(testredirecturl)).thenReturn(url);


        var response = controller.getAuthUrl(testredirecturl);
        assertEquals(url, response.getBody());
    }

    @Test
    public void Test_Controller_getToken_Mock_KeycloakService_findAuthUrl_And_keycloakRequest() throws UnirestException {

        AuthAccess access = new AuthAccess();
        access.setAccess_code("abc");
        access.setCode("123");
        AuthUrl url = new AuthUrl();
        url.setUrl("url");

        Mockito.when(keycloakService.findAuthUrl(access)).thenReturn(url);
        var response = controller.getAuthUrl(testredirecturl); //3000?
        try {
            Mockito.when(keycloakService.keycloakTokenRequest(access.getCode(), url)).thenReturn("token");
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        var test = controller.getToken(access);

        assertEquals("token", test.getBody());
    }


    @Test
    public void test_Refreshtoken_and_Token_Integation_Test() throws Exception {

        HttpResponse<String> response =
                Unirest.post(token_endpoint)
                        .header("content-type", "application/x-www-form-urlencoded")
                        .body("grant_type=password&client_id=" + keycloak_client_id
                                + "&password=" + testpassword
                                + "&username=" + testusername
                                // +"&redirect_uri="+auth.getRedirect() // brauche ich nicht? weil in keycloak console
                                + "&client_secret=" + client_secret
                        )
                        .asString();
        Assert.assertTrue(response.getBody().contains("access_token"));
        Assert.assertTrue(response.getBody().contains("refresh_token"));


        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
        String refreshToken = (String) map.get("refresh_token");

        RefreshTokenAccess tokenAccess = new RefreshTokenAccess();
        tokenAccess.setRefresh_token(refreshToken);

        HttpResponse<String> responseToken =
                Unirest.post(testrefreshtokenapi)
                        .header("content-type", "application/json")
                        .body("{\"refresh_token\": \"" + refreshToken + "\"}"
                        )
                        .asString();

        System.out.println("responseToken: " + responseToken.getBody());
        Assert.assertTrue(responseToken.getBody().contains("access_token"));

        ObjectMapper mapperS = new ObjectMapper();
        Map<String, Object> mapS = mapperS.readValue(response.getBody(), Map.class);
        String accessToken = (String) mapS.get("access_token");


        HttpResponse<String> responseTest =
                Unirest.get(testuserapi)
                        .header("authorization", "Bearer " + accessToken)

                        .asString();

        System.out.println(responseTest.getBody());
        Assert.assertTrue(responseTest.getBody().contains("Email"));
    }
}
