package com.keycloak.starterpaket.controller;

import com.keycloak.starterpaket.KeycloakstarterpaketApplication;
import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.keycloak.starterpaket.service.KeycloakService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = KeycloakstarterpaketApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mvc;
    private KeycloakService keycloakService;
    private AuthController controller;

    @Value("${test-redirect-url}")
    private String testredirecturl;
    @Value("${test-full-redirect-url}")
    private String testfullredirecturl;

    @BeforeEach
    public void setup() {
        keycloakService = mock(KeycloakService.class);
        controller = new AuthController(keycloakService);
    }

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

    @Test
    public void Test_Controller_getAuthUrl_Mock_KeycloakService_generateAuthUrl() throws Exception {

        AuthUrl url = new AuthUrl();
        url.setUrl("url");
        Mockito.when(keycloakService.generateAuthUrl(testredirecturl)).thenReturn(url);
        var response = controller.getAuthUrl(testredirecturl);
        Assertions.assertEquals(url, response.getBody());
    }

    @Test
    public void Test_Controller_getToken_Mock_KeycloakService_findAuthUrl_And_keycloakRequest() throws UnirestException {

        AuthAccess access = new AuthAccess();
        access.setAccess_code("abc");
        access.setAuthorization_code("123");
        AuthUrl url = new AuthUrl();
        url.setUrl("url");

        Mockito.when(keycloakService.findAuthUrl(access)).thenReturn(url);
        try {
            Mockito.when(keycloakService.keycloakTokenRequest(access.getAuthorization_code(), url)).thenReturn("token");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        var test = controller.getToken(access);
        Assertions.assertEquals("token", test.getBody());
    }
}