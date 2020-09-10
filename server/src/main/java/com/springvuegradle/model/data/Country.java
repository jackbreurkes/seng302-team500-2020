package com.springvuegradle.model.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model class for the countries that will be stored
 * in our database after being pulled from the RESTCountries API
 */
@Entity
@Table(name="country")
public class Country implements Serializable {

    /**
     * ISO country code for searching
     */
    @Id
    public int numericCode;


    /**
     * Country name
     */
    public String name;

    /**
     * Constructor requried by spring
     */

    protected Country() {}

    public Country(int numericCode, String name){
        this.numericCode = numericCode;
        this.name = name;
    }

    public int getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(int numericCode) {
        this.numericCode = numericCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String countryName) {
        this.name = countryName;
    }
}
