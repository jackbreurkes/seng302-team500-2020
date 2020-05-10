package com.springvuegradle.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.User;

/**
 * JPA Repository of users
 * @author Alex Hobson
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	public List<User> getSuperAdmin();
	public int superAdminExists();

}
