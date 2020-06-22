package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    public Hashtag findByText(String text);
}
