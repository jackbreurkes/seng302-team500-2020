package com.springvuegradle.model.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model class for the countries that will be stored
 * in our database after being pulled from the RESTCountries API
 * @Author Michael Freeman
 */


@Entity
@Table(name="country")
public class Country implements Serializable {

    /**
     * ISO country code for searching
     */
    @Id
    public int code;

    /**
     * Country name
     */
    public String countryName;

    /**
     * Constructor requried by spring
     */

    protected Country() {}

    public Country(int numericCode, String name){
        this.code = code;
        this.countryName = countryName;
    }

    public int getIsoCode() {
        return code;
    }

    public void setIsoCode(int isoCode) {
        this.code = isoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
