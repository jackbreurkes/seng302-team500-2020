package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

}
