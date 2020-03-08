package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Country;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repositort of countries
 *
 * @Author Michael Freeman
 */
public interface CountryRepository extends JpaRepository<Country, Integer> {
}
