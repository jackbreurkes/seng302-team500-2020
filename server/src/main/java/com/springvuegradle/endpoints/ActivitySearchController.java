package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.responses.ActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @return List of activity response objects
     * @throws UserNotAuthenticatedException
     * @throws InvalidRequestFieldException
     * @throws  RecordNotFoundException
     */
    @GetMapping
    @CrossOrigin
    public List<ActivityResponse> searchActivities(@RequestParam(value="searchTerms")String[] requestSearchTerms,
                                                   @RequestParam(value="page") int page,
                                                   @RequestParam(value="pageSize")
                                                   HttpServletRequest request) throws UserNotAuthenticatedException, InvalidRequestFieldException, RecordNotFoundException {

        UserAuthorizer.getInstance().checkIsAuthenticated(request);
        ArrayList<String> searchTerms = new ArrayList<>(Arrays.asList(requestSearchTerms));
        //Expecting frontend to strip/split strings appropriate, for both quotation marks and space separated. Pages start at 0
        if(searchTerms.size() == 0){
            throw new InvalidRequestFieldException("No Search Terms Entered");
        }
        List<ActivityResponse> searchResults = new ArrayList<>();
        List<ActivityResponse> highPrioritySearchResults = new ArrayList<>();
        for(String term : searchTerms){
            List<Activity> activitySearchResults = activityRepository.findActivitiesByActivityNameContaining(term);
            for(Activity activity : activitySearchResults){
                //check not already in list
                ActivityResponse activityResponse = new ActivityResponse(activity);
                searchResults.add(new ActivityResponse(activity));
            }
        }
        if(searchResults.size() == 0){
            throw new RecordNotFoundException("No Activities Found");
        }else if(searchResults.size() <= 25){
            //Return everything as there are not enough results to paginatie
            return searchResults;
        }else{
            //Pagination
            int startIndex = 25 * page;
            if(startIndex >= searchResults.size()){
                throw new InvalidRequestFieldException("Page index out of range");
            }
            int endIndex = startIndex + 25;
            if(searchResults.size() < endIndex){
                endIndex = searchResults.size();
            }
            return searchResults.subList(startIndex, endIndex);
        }
    }
}
