package com.springvuegradle.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Admin;

/**
 * JPA Repository of users
 * @author Alex Hobson
 *
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

}
