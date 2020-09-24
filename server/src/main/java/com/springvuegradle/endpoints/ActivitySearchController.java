package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.responses.ActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activities")
public class ActivitySearchController {

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Endpoint for searching activities by name
     * @param request the request
     * @param requestSearchTerms the search terms being requested
     * @param page the page number to return
     * @param pageSize the size of the pages being used
     * @return List of activity response objects
     * @throws UserNotAuthenticatedException if user not authenticated
     * @throws InvalidRequestFieldException if a parameter is not in the correct format
     */
    @GetMapping
    @CrossOrigin
    public List<ActivityResponse> searchActivities(@RequestParam(value="searchTerms")String[] requestSearchTerms,
                                                   @RequestParam(value="page") int page,
                                                   @RequestParam(value="pageSize") int pageSize,
                                                   HttpServletRequest request) throws UserNotAuthenticatedException, InvalidRequestFieldException {

        UserAuthorizer.getInstance().checkIsAuthenticated(request);
        ArrayList<String> searchTerms = new ArrayList<>(Arrays.asList(requestSearchTerms));
        //Expecting frontend to strip/split strings appropriate, for both quotation marks and space separated. Pages start at 0
        List<String> queryTerms = searchTerms.stream().filter(term -> term.length() > 0).collect(Collectors.toList());
        if(queryTerms.isEmpty()) {
            throw new InvalidRequestFieldException("No non-empty search terms were entered");
        }
        if (page < 0) {
            throw new InvalidRequestFieldException("page number must be non-negative");
        }
        if(pageSize <= 0){
            throw new InvalidRequestFieldException("page size must be at least one");
        }
        List<Activity> searchResults = activityRepository.findUniqueActivitiesByListOfNames(queryTerms, PageRequest.of(page, pageSize));
        return searchResults.stream().map(ActivityResponse::new).collect(Collectors.toList());

    }
}
