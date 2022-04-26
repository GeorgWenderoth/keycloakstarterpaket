package com.keycloak.starterpaket.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.*;


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

    @JsonIgnore
    LocalTime localTime;

    private String url;
    private String access_code;

   /* @JsonIgnore
    private Date timestamp;

    @JsonIgnore

    private Timer timer;



    @JsonIgnore
    Calendar calendar;

*/




}