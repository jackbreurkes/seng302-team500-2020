package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}
