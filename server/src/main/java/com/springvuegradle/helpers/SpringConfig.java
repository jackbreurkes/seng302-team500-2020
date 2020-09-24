package com.springvuegradle.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.model.data.Country;
import com.springvuegradle.model.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RestService restService;


    @Scheduled(cron = "0 0 0 * * SAT")
    public void checkPassportCountries(){
        String apiCountriesJSON = restService.getCountriesJSON("https://restcountries.eu/rest/v2/all?fields=name;numericCode");
        List<Country> countries = processJson(apiCountriesJSON);
        for(Country country : countries){
            //Check if it exists in the repo
            if(countryRepository.existsById(country.numericCode)){
                //checking if names are the same
                Country tempCountry = countryRepository.findById(country.numericCode).get();
                if(!(tempCountry.name).equals(country.name)){
                     //then the name is wrong and needs to be updated
                    countryRepository.findById(country.numericCode).get().setName(country.name);
                 }
                 //leaves
            } else {
                //Country does not exist in database and needs to be added
                countryRepository.save(country);
            }
            //TODO Check when a country is removed from the API


        }
    }

    public List<Country> processJson(String rawJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Country> countryList = objectMapper.readValue(rawJson, new TypeReference<List<Country>>(){});
            return countryList;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
