package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Country;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    public Optional<Country> findByName(String name);
}
