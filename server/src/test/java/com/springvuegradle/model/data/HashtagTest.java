package com.springvuegradle.model.data;

import com.springvuegradle.endpoints.ActivitiesController;
import com.springvuegradle.model.repository.HashtagRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class HashtagTest {

    @Autowired
    HashtagRepository hashtagRepository;

    @Test
    public void saveNewHashtagSuccessful() {
        String text = "blacklivesmatter";
        Hashtag hashtag = new Hashtag(text);
        hashtagRepository.save(hashtag);
        Hashtag result = hashtagRepository.findByText(text);
        assertEquals(text, result.getText());
    }

    @Test
    public void findNonExistentHashtagReturnsNull() {
        String text = "kiakaha";
        Hashtag result = hashtagRepository.findByText(text);
        assertNull(result);
    }

    @Test
    public void saveWithNonUniqueTextFails() {
        String text = "goodvibes";
        Hashtag hashtag = new Hashtag(text);
        Hashtag duplicate = new Hashtag(text);
        hashtagRepository.save(hashtag);
        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class, () -> {
            hashtagRepository.save(duplicate);
        });
        ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
        String constraintColumn = "on public.hashtag(text)";  // the column on which the constraint appears
        assertTrue(cause.getConstraintName().toLowerCase().contains(constraintColumn));
    }

    @Test
    public void saveMultipleWithDifferentTextSuccessful() {
        String firstText = "unique";
        String secondText = "alsounique";
        Hashtag firstHashtag = new Hashtag(firstText);
        Hashtag secondHashtag = new Hashtag(secondText);
        hashtagRepository.save(firstHashtag);
        hashtagRepository.save(secondHashtag);
        assertNotNull(hashtagRepository.findByText(firstText));
        assertNotNull(hashtagRepository.findByText(secondText));
    }

}
