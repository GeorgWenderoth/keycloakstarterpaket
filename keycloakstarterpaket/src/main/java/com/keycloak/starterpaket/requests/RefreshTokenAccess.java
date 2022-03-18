package com.keycloak.starterpaket.requests;

import lombok.Data;

@Data
public class RefreshTokenAccess {
    private String refresh_token;
}
