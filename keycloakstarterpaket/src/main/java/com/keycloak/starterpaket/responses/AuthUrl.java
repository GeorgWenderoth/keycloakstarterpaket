package com.keycloak.starterpaket.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalTime;

@Data
public class AuthUrl {
    @JsonIgnore
    private String code_verifier;
    @JsonIgnore
    private String code_challenge;
    @JsonIgnore
    private String redirect;
    @JsonIgnore
    LocalTime localTime;

    private String url;
    private String access_code;
}