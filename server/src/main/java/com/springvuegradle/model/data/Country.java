package com.springvuegradle.model.data;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
