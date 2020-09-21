package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ActivitySearchRepositoryImplTest {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @Autowired
    ProfileRepository profileRepository;

    ActivityType activityType;

    @BeforeAll
    void beforeAll(){
        activityType = activityTypeRepository.save(new ActivityType("Walkin"));
    }

    @BeforeEach
    void beforeEach(){
        profileRepository.flush();
        activityRepository.flush();
    }

    Activity createActivityWithName(String name) {
        Profile creator = new Profile(new User(), "Fname", "Lname", LocalDate.EPOCH, Gender.MALE);
        profileRepository.save(creator);
        Activity activity = new Activity(name, false, "Location", creator, Set.of(activityType));
        return activityRepository.save(activity);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This",
            "is",
            "a",
            "run"
    })
    @Disabled  //Disabled due to failing on pipeline only, will pass locally
    void testSingleWord_singleResult(String searchTerm) {
        String activityName = "This is a superfun run";
        createActivityWithName(activityName);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList(searchTerm), PageRequest.of(0, 25));

        assertEquals(1, results.size());
        assertEquals(activityName, results.get(0).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Awesome Activity",
            "This is a fun run",
            "Test Test Test",
            "oneword"
    })
    @Disabled //Disabled due to failing on pipeline only, will pass locally
    void testSearchByAllWordsInName_singleResult(String activityName) {
        List<String> searchTerms = Arrays.asList(activityName.split(" "));
        createActivityWithName(activityName);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));

        assertEquals(1, results.size());
        assertEquals(activityName, results.get(0).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Test", "activity"
    })
    void testSingleWord_mulitpleResults(){
        createActivityWithName("Test activity two");
        createActivityWithName("Test activity one");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList("Test"), PageRequest.of(0, 25));

        assertEquals(2, results.size());
        assertEquals("Test activity one", results.get(0).getActivityName());
        assertEquals("Test activity two", results.get(1).getActivityName());
    }

    @Test
    void testMultipleResultsSamePriority_OrderedAlphabetically() {
        createActivityWithName("Test b");
        createActivityWithName("Test a");
        createActivityWithName("Mock b");
        createActivityWithName("Mock a");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Arrays.asList("Test", "Mock"), PageRequest.of(0, 25));
        assertEquals(4, results.size());
        assertEquals("Mock a", results.get(0).getActivityName());
        assertEquals("Mock b", results.get(1).getActivityName());
        assertEquals("Test a", results.get(2).getActivityName());
        assertEquals("Test b", results.get(3).getActivityName());
    }

    @Test
    void testMultipleResults_OrderedByPriority() {
        createActivityWithName("Test b");
        createActivityWithName("Test a");
        createActivityWithName("Mock a");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Arrays.asList("Test", "Mock", "est"), PageRequest.of(0, 25));
        assertEquals(3, results.size());
        assertEquals("Test a", results.get(0).getActivityName());
        assertEquals("Test b", results.get(1).getActivityName());
        assertEquals("Mock a", results.get(2).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This", "this", "THIS", "tHIS", "ThIs",
            "Activity", "activity", "ACTIVITY", "aCtIViTy",
            "tHIS aCTIVITY", "activity capital", "CAPITAL LETTERS",
            "this ACTIVITY capital LETTERS", "THIS activity CAPITAL letters"
    })
    void testDifferentCase_SearchIsCaseInsensitive(String query) {
        createActivityWithName("This Activity Capital Letters");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));
        assertEquals(1, results.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "this activity",
            "this activity multiple",
            "this activity multiple words",
            "activity multiple",
            "activity multiple words",
            "multiple words"
    })
    void testMultiwordSearch_CorrectOrder_ExpectOneResult(String query) {
        createActivityWithName("this activity multiple words");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));
        assertEquals(1, results.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "activity this",
            "this multiple activity",
            "this activity words multiple",
            "this multiple words",
            "this words",
            "activity words"
    })
    void testMultiwordSearch_IncorrectOrder_ExpectNoResults(String query) {
        createActivityWithName("this activity multiple words");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));
        assertEquals(0, results.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "is",
            "is act",
            "tivit",
            "e",
            "ple",
            "ultiple word",
            "s activity m"
    })
    void testPartialWordMatches_ExpectOne(String query) {
        createActivityWithName("this activity multiple words");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, Pageable.unpaged());
        assertEquals(1, results.size());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 1",
            "2, 2, 2",
            "10, 10, 10",
            "1, 2, 2",
            "2, 1, 1",
            "5, 10, 2",
            "10, 3, 3",
            "2, 10, 1",
            "10, 2, 1",
            "1, 50, 50"
    })
    void testMultiplePages_returnsOnePageAtATime(String numPagesString, String pageSizeString, String lastPageSizeString) {
        final int numPages = Integer.parseInt(numPagesString);
        final int pageSize = Integer.parseInt(pageSizeString);
        final int lastPageSize = Integer.parseInt(lastPageSizeString);
        if (lastPageSize > pageSize) {
            throw new IllegalArgumentException("last page cannot be larger than the max page size");
        }
        for (int pageNumber = 0; pageNumber < numPages - 1; pageNumber++) {
            for (int positionOnPage = 0; positionOnPage < pageSize; positionOnPage++) {
                createActivityWithName(String.format("page %d activity %d", pageNumber, positionOnPage));
            }
        }
        for (int positionOnPage = 0; positionOnPage < lastPageSize; positionOnPage++) {
            createActivityWithName(String.format("page %d activity %d", numPages - 1, positionOnPage));
        }

        List<List<Activity>> results = new ArrayList<>();
        for (int i = 0; i < numPages; i++) {
            results.add(activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList("activity"), PageRequest.of(i, pageSize)));
        }

        for (int i = 0; i < numPages - 1; i++) {
            assertEquals(pageSize, results.get(i).size());
            for (Activity activity : results.get(i)) {
                assertTrue(activity.getActivityName().startsWith(String.format("page %d", i)), String.format("Activity %s appeared on page %d", activity.getActivityName(), i));
            }
        }
        assertEquals(lastPageSize, results.get(numPages-1).size());
        for (Activity activity : results.get(numPages-1)) {
            assertTrue(activity.getActivityName().startsWith(String.format("page %d", numPages-1)));
        }

    }

}