package com.springvuegradle.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    public Optional<Location> findLocationByCityAndCountry(String city, String country);
}
