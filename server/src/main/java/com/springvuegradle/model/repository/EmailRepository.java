package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Email;

/**
 * JPA Repository of email addresses
 * @author Alex Hobson
 * @author Olivia Mackintosh
 *
 */
public interface EmailRepository extends JpaRepository<Email, String> {

	public Email findByEmail(String email);
	public int getNumberOfEmails(User user);
	public String getPrimaryEmail(User u);
	public List<Email> getNonPrimaryEmails(User u);
}
