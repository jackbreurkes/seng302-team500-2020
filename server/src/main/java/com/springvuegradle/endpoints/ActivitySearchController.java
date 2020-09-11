package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.requests.SearchActivityRequest;
import com.springvuegradle.model.responses.ActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivitySearchController {

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Endpoint for searching activities by name
     * @param searchActivityRequest the list of search terms in request format
     * @param request the request
     * @return List of activity response objects
     * @throws UserNotAuthenticatedException
     * @throws InvalidRequestFieldException
     * @throws  RecordNotFoundException
     */
    @GetMapping
    @CrossOrigin
    public List<ActivityResponse> searchActivities(@Valid @RequestBody SearchActivityRequest searchActivityRequest, HttpServletRequest request) throws UserNotAuthenticatedException, InvalidRequestFieldException, RecordNotFoundException {

        UserAuthorizer.getInstance().checkIsAuthenticated(request);
        //Expecting frontend to strip/split strings appropriate, for both quotation marks and space separated
        List<String> searchTerms = searchActivityRequest.getSearchTerms();
        if(searchTerms.size() == 0){
            throw new InvalidRequestFieldException("No Search Terms Entered");
        }
        List<ActivityResponse> searchResults = new ArrayList<>();
        for(String term : searchTerms){
            List<Activity> activitySearchResults = activityRepository.findActivitiesByActivityNameContaining(term);
            for(Activity activity : activitySearchResults){
                searchResults.add(new ActivityResponse(activity));
            }
        }
        if(searchResults.size() == 0){
            throw new RecordNotFoundException("No Activities Found");
        }
        return searchResults;
    }
}
