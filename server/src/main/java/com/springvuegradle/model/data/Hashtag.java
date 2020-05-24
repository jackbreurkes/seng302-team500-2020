package com.springvuegradle.model.data;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * JPA POJO to represent a hashtag
 */
@Entity
@Table(name="hashtag")
public class Hashtag {

    @Id
    private long hashtagId;

    @Column(columnDefinition = "varchar(30) not null", unique = true)
    private String text;

    /**
     * no arg constructor required by JPA
     */
    public Hashtag() {}

    /**
     * creates a new hashtag with the given text.
     * @param hashtagText unique text to display after the hashtag character
     */
    public Hashtag(String hashtagText) {
        this.text = hashtagText;
    }

    /**
     * @return the unique primary key ID of this hashtag
     */
    public long getHashtagId() {
        return hashtagId;
    }

    /**
     * @return the text associated with this hashtag
     */
    public String getText() {
        return text;
    }
}
