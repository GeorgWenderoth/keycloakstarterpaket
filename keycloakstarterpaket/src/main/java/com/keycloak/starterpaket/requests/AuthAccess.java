package com.keycloak.starterpaket.requests;

import lombok.Data;

@Data
public class AuthAccess {
    private String code;
    private String access_code;
}
