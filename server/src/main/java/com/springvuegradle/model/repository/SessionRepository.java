package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface SessionRepository extends JpaRepository<Session, String> {

	@Deprecated
    public Session findSessionByUser(User user);

	@Transactional
    @Modifying
    public void deleteAllByUser(User user);

}
