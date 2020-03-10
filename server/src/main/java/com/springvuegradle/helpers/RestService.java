package com.springvuegradle.helpers;

import com.springvuegradle.model.data.Country;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implements get request for all countries
 * @Author Michael Freeman
 * @Author Josh Yee
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
