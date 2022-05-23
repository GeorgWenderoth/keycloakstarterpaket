package com.keycloak.starterpaket.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAccess {
    private String authorization_code;
    private String access_code;
}
