package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    public Optional<Location> findLocationByCityAndCountry(String city, String country);
}
