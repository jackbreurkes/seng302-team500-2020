package com.springvuegradle.helpers;

import com.springvuegradle.model.data.Country;
import com.springvuegradle.model.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RestService restService;


    //@Scheduled(cron = "0 0 12 ? * MON *")
    @Scheduled(fixedRate = 1000)
    public void checkPassportCountries(){
        String apiCountriesJSON = restService.getCountriesJSON("https://restcountries.eu/rest/v2/all");
        System.out.println(apiCountriesJSON);
        //TODO
        ArrayList<Country> apiCountries = new ArrayList<Country>();
        apiCountries.add(new Country(2233, "ASad"));
        for(Country country : apiCountries){
            //Check if it exists in the repo
            if(countryRepository.existsById(country.code)){
                //checking if names are the same
                 if(!countryRepository.getOne(country.code).countryName.equals(country.countryName)){
                     //then the name is wrong and needs to be updated
                    countryRepository.getOne(country.code).setCountryName(country.countryName);
                 }
            } else {
                //Country does not exist in database and needs to be added
                countryRepository.save(country);
            }
            System.out.println(apiCountries);
            //TODO Check when a country is removed from the API


        }
    }
}
