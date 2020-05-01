package com.springvuegradle.endpoints;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.repository.ActivityTypeRepository;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ActivityTypeController.class})
@WebMvcTest
public class ActivityTypeControllerTest {

	@InjectMocks
	private ActivityTypeController activityTypeController;
	
	@Autowired
    private MockMvc mvc;

    @MockBean
    private ActivityTypeRepository repo;
	
	@BeforeEach
    void beforeEach() {
        activityTypeController = new ActivityTypeController();
    }
	
    @Test
    public void testGetNoActivities() throws Exception {
    	ArrayList<ActivityType> mockActivities = new ArrayList<ActivityType>();
    	
    	Mockito.when(repo.findAll()).thenReturn(mockActivities);
    	
        mvc.perform(MockMvcRequestBuilders
                .get("/activities")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
    
    @Test
    public void testGetOneActivity() throws Exception {
    	ArrayList<ActivityType> mockActivities = new ArrayList<ActivityType>();
    	mockActivities.add(new ActivityType("driving"));
    	Mockito.when(repo.findAll()).thenReturn(mockActivities);
    	
        mvc.perform(MockMvcRequestBuilders
                .get("/activities")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("driving"));
    }
    
    @Test
    public void testGetManyActivities() throws Exception {
    	ArrayList<ActivityType> mockActivities = new ArrayList<ActivityType>();
    	mockActivities.add(new ActivityType("driving"));
    	mockActivities.add(new ActivityType("flying"));
    	mockActivities.add(new ActivityType("teleporting"));
    	Mockito.when(repo.findAll()).thenReturn(mockActivities);
    	
        mvc.perform(MockMvcRequestBuilders
                .get("/activities")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));
    }
}
