package com.springvuegradle.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    public Optional<Profile> findById(User user);
	public List<Profile> findByNickNameStartingWith(String nickname);
}
