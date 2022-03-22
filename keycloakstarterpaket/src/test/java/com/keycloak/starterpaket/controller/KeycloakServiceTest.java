package com.keycloak.starterpaket.controller;


import com.keycloak.starterpaket.requests.AuthAccess;
import com.keycloak.starterpaket.responses.AuthUrl;
import com.keycloak.starterpaket.service.KeycloakService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class KeycloakServiceTest {

    private  KeycloakService keycloakService = new KeycloakService();

    @Value("${keycloak.resource}")
    private String keycloak_client_id;

    @Value("${token-endpoint}")
    private String token_endpoint;

    @Test
    public void generateAuthUrlTest() throws Exception {

       var test = keycloakService.generateAuthUrl("http://localhost:3000/*");
        String url = test.getUrl();

        Assert.assertTrue(url.contains("redirect_uri=http://localhost:3000/*"));
        Assert.assertTrue(url.contains("response_type=code"));
        Assert.assertTrue(url.contains("code_challenge"));
        Assert.assertTrue(url.contains("code_challenge_method=S256"));
        Assert.assertTrue(url.contains("client_id"));
    }

    @Test
    public void test_generateAuthUrlSecondTest() throws Exception {
        var test = keycloakService.generateAuthUrl("zapp");
        String url = test.getUrl();
        Assert.assertTrue(url.contains("redirect_uri=zapp"));
        Assert.assertFalse(url.contains("redirect_uri=http://localhost:3000/*"));
    }

    @Test
    public void test_findAuthUrl(){
        AuthAccess access = new AuthAccess();
        access.setCode("123");
        access.setAccess_code("456");
        var authUrl = new AuthUrl();
        authUrl.setAccess_code("456");
        AuthUrl.urls.add(authUrl);
        var test = keycloakService.findAuthUrl(access);
        assertEquals(authUrl, test);
    }

    @Test
    public void test_findAuthUrl_withwrongcode_expectNull(){
        AuthAccess access = new AuthAccess();
        access.setCode("123");
        access.setAccess_code("123");
        var authUrl = new AuthUrl();
        authUrl.setAccess_code("456");
        AuthUrl.urls.add(authUrl);
        var test = keycloakService.findAuthUrl(access);
        assertNull(test);
    }
}
