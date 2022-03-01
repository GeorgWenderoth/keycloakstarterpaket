package com.keycloak.starterpaket.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeycloakUser {
    private String username;
    private String password;
}
