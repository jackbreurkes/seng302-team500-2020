package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.ActivityResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivitySearchControllerTest {
    @InjectMocks
    private ActivitySearchController activitySearchController;

    @Mock
    private ActivityRepository activityRepository;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void searchActivityNotAuthenticated() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();

        String terms[] = new String[1];
        terms[0] = "Test";

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitySearchController.searchActivities(terms, 0, 25, request);
        });
    }

    @Test
    void searchActivityNoResults_ReturnsEmptyList() throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Mockito.when(activityRepository.findUniqueActivitiesByListOfNames(Mockito.anyList(), Mockito.any(PageRequest.class))).thenReturn(new ArrayList<>());

        List<ActivityResponse> responses = activitySearchController.searchActivities(new String[] { "Terms" }, 0, 25, request);
        assertTrue(responses.isEmpty());
    }

    @Test
    void searchNoTerms_InvalidFieldException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        String[] terms = new String[0];
        InvalidRequestFieldException e = assertThrows(InvalidRequestFieldException.class, () -> {
            activitySearchController.searchActivities(terms, 0, 25, request);
        });
        assertEquals("No non-empty search terms were entered", e.getMessage());
    }

    @Test
    void searchWithEmptyTerms_InvalidFieldException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        String[] terms = new String[] {"", "", ""};
        InvalidRequestFieldException e = assertThrows(InvalidRequestFieldException.class, () -> {
            activitySearchController.searchActivities(terms, 0, 25, request);
        });
        assertEquals("No non-empty search terms were entered", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 25",
            "5, 10",
            "1, 1",
            "0, 1",
            "10, 3"
    })
    void searchActivityPaginateFirstPage_usesRepositoryWithPagination(String pageNumberString, String pageSizeString) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        final int pageNumber = Integer.parseInt(pageNumberString);
        final int pageSize = Integer.parseInt(pageSizeString);

        String terms[] = new String[1];
        terms[0] = "Test";
        Mockito.reset(activityRepository);
        Mockito.when(activityRepository.findUniqueActivitiesByListOfNames(Mockito.anyList(), Mockito.any(PageRequest.class))).thenReturn(new ArrayList<>());

        List<ActivityResponse> response = activitySearchController.searchActivities(terms, pageNumber, pageSize, request);

        ArgumentCaptor<List<String>> searchTermCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        Mockito.verify(activityRepository).findUniqueActivitiesByListOfNames(searchTermCaptor.capture(), pageRequestCaptor.capture());
        assertEquals(1, searchTermCaptor.getAllValues().size());
        assertEquals(1, pageRequestCaptor.getAllValues().size());
        PageRequest pageRequest = pageRequestCaptor.getValue();
        assertTrue(pageRequest.isPaged());
        assertEquals(pageNumber, pageRequest.getPageNumber());
        assertEquals(pageSize, pageRequest.getPageSize());
    }

    @ParameterizedTest
    @ValueSource(ints = {
            -1, -4, -5, -10000
    })
    void searchActivityNegativePageNumber_InvalidFieldException(int pageNum) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        String[] terms = new String[1];
        terms[0] = "Test";
        InvalidRequestFieldException e = assertThrows(InvalidRequestFieldException.class, () -> {
            activitySearchController.searchActivities(terms, pageNum, 25, request);
        });
        assertEquals("page number must be non-negative", e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {
            -1, -4, -5, -10000, 0
    })
    void searchActivityPageSizeLessThanOne_InvalidFieldException(int pageSize) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        String[] terms = new String[1];
        terms[0] = "Test";
        InvalidRequestFieldException e = assertThrows(InvalidRequestFieldException.class, () -> {
            activitySearchController.searchActivities(terms, 0, pageSize, request);
        });
        assertEquals("page size must be at least one", e.getMessage());
    }
}

