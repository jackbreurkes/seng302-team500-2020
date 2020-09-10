package com.springvuegradle.helpers;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implements get request for all countries
 */

@Service
public class RestService {

    //todo learn about this
    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Takes a url and returns country objects
     * @param url
     * @return countries list
     */

    public String getCountriesJSON(String url){
        return this.restTemplate.getForObject(url, String.class);
    }
}
