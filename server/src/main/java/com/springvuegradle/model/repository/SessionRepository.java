package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {

    public Session findSessionByUser(User user);

}
