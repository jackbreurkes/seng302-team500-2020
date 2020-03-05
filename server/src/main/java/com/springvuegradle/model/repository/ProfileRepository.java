package com.springvuegradle.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.Gender;
import java.util.Date;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    public Profile findById(User user);
    public void updateFitness(User user, int fitness);
}
