package com.springvuegradle.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Role;

/**
 * JPA Repository of roles
 *
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	public Role findByRolename(String rolename);

}
