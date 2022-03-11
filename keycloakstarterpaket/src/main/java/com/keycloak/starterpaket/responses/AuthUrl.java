package com.keycloak.starterpaket.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class AuthUrl {
    @JsonIgnore
    public static List<AuthUrl> urls = new ArrayList<>();
    @JsonIgnore
    private String verifier;
    @JsonIgnore
    private String challenge;
    @JsonIgnore
    private String redirect;

    private String url;
    private String access_code;
}