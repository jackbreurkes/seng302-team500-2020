package com.springvuegradle.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    public Optional<Country> findByName(String name);
}
