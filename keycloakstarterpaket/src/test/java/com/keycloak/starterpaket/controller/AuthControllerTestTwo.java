package com.keycloak.starterpaket.controller;

import com.keycloak.starterpaket.KeycloakstarterpaketApplication;
import com.keycloak.starterpaket.requests.AuthAccess;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//3-15
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = KeycloakstarterpaketApplication.class)
@AutoConfigureMockMvc
/*@TestPropertySource(
        locations = "application-integrationtest.properties"
)*/
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)

public class AuthControllerTestTwo {
    @Autowired
    private MockMvc mvc;



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
    public void testToken_MockKeycloak() throws Exception{

        mvc.perform(post("/api/auth/token")
                .contentType(MediaType.ALL).content( "code=4ff657a2-5c5e-445e-a992-4995e2040148.6e118648-00d2-497f-b915-4b157f748bb2.846fa4fa-ed04-46ab-ac2b-0ad0e8c5dc43&access_code=837e1bd4x28cbx4c75x8d1bx639894410369"))

                .andExpect(status().isOk());

    }


}
