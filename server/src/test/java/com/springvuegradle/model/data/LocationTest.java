package com.springvuegradle.model.data;

import com.springvuegradle.model.repository.ActivityPinRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

public class LocationTest {

	private final String christchurchLocationQuery = "[\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 293493313,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"relation\",\r\n" + 
			"        \"osm_id\": 2730349,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"-43.6292014\",\r\n" + 
			"            \"-43.3890866\",\r\n" + 
			"            \"172.3930248\",\r\n" + 
			"            \"172.8216267\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"-43.530955\",\r\n" + 
			"        \"lon\": \"172.6366455\",\r\n" + 
			"        \"display_name\": \"Christchurch, Christchurch City, Canterbury, New Zealand\",\r\n" + 
			"        \"class\": \"place\",\r\n" + 
			"        \"type\": \"city\",\r\n" + 
			"        \"importance\": 0.7063371164819315,\r\n" + 
			"        \"icon\": \"https://nominatim.openstreetmap.org/images/mapicons/poi_place_city.p.20.png\",\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"city\": \"Christchurch\",\r\n" + 
			"            \"county\": \"Christchurch City\",\r\n" + 
			"            \"state\": \"Canterbury\",\r\n" + 
			"            \"country\": \"New Zealand\",\r\n" + 
			"            \"country_code\": \"nz\"\r\n" + 
			"        }\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 108626,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"node\",\r\n" + 
			"        \"osm_id\": 14600264,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"50.6944198\",\r\n" + 
			"            \"50.7744198\",\r\n" + 
			"            \"-1.80512\",\r\n" + 
			"            \"-1.72512\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"50.7344198\",\r\n" + 
			"        \"lon\": \"-1.76512\",\r\n" + 
			"        \"display_name\": \"Christchurch, South West England, England, BH23 1LJ, United Kingdom\",\r\n" + 
			"        \"class\": \"place\",\r\n" + 
			"        \"type\": \"town\",\r\n" + 
			"        \"importance\": 0.5570923861518361,\r\n" + 
			"        \"icon\": \"https://nominatim.openstreetmap.org/images/mapicons/poi_place_town.p.20.png\",\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"town\": \"Christchurch\",\r\n" + 
			"            \"state_district\": \"South West England\",\r\n" + 
			"            \"state\": \"England\",\r\n" + 
			"            \"postcode\": \"BH23 1LJ\",\r\n" + 
			"            \"country\": \"United Kingdom\",\r\n" + 
			"            \"country_code\": \"gb\"\r\n" + 
			"        }\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 273206734,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"relation\",\r\n" + 
			"        \"osm_id\": 130878,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"50.7205956\",\r\n" + 
			"            \"50.8096156\",\r\n" + 
			"            \"-1.8738931\",\r\n" + 
			"            \"-1.6816655\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"50.7651261\",\r\n" + 
			"        \"lon\": \"-1.8145483592492209\",\r\n" + 
			"        \"display_name\": \"Christchurch, Hurn, Bournemouth, Christchurch and Poole, South West England, England, United Kingdom\",\r\n" + 
			"        \"class\": \"boundary\",\r\n" + 
			"        \"type\": \"historic\",\r\n" + 
			"        \"importance\": 0.5570923861518361,\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"boundary\": \"Christchurch\",\r\n" + 
			"            \"hamlet\": \"Hurn\",\r\n" + 
			"            \"village\": \"Hurn\",\r\n" + 
			"            \"county\": \"Bournemouth, Christchurch and Poole\",\r\n" + 
			"            \"state_district\": \"South West England\",\r\n" + 
			"            \"state\": \"England\",\r\n" + 
			"            \"country\": \"United Kingdom\",\r\n" + 
			"            \"country_code\": \"gb\"\r\n" + 
			"        }\r\n" + 
			"    }\r\n" + 
			"]";
	
	private final String rejectNonCities = "[\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 95473420,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"way\",\r\n" + 
			"        \"osm_id\": 34365960,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"-43.5227676\",\r\n" + 
			"            \"-43.522213\",\r\n" + 
			"            \"172.5807742\",\r\n" + 
			"            \"172.5816012\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"-43.522490250000004\",\r\n" + 
			"        \"lon\": \"172.58119446991861\",\r\n" + 
			"        \"display_name\": \"Jack Erskine, Science Road, Ilam, Christchurch, Christchurch City, Canterbury, 8041, New Zealand\",\r\n" + 
			"        \"class\": \"building\",\r\n" + 
			"        \"type\": \"university\",\r\n" + 
			"        \"importance\": 0.201,\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"building\": \"Jack Erskine\",\r\n" + 
			"            \"road\": \"Science Road\",\r\n" + 
			"            \"suburb\": \"Ilam\",\r\n" + 
			"            \"city\": \"Christchurch\",\r\n" + 
			"            \"county\": \"Christchurch City\",\r\n" + 
			"            \"state\": \"Canterbury\",\r\n" + 
			"            \"postcode\": \"8041\",\r\n" + 
			"            \"country\": \"New Zealand\",\r\n" + 
			"            \"country_code\": \"nz\"\r\n" + 
			"        }\r\n" + 
			"    }\r\n" + 
			"]";
	
	private final String nonFirstOption = "[\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 235154276,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"relation\",\r\n" + 
			"        \"osm_id\": 341906,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"34.271821\",\r\n" + 
			"            \"35.051293\",\r\n" + 
			"            \"135.0258879\",\r\n" + 
			"            \"135.746603\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"34.6198813\",\r\n" + 
			"        \"lon\": \"135.490357\",\r\n" + 
			"        \"display_name\": \"Osaka Prefecture, Japan\",\r\n" + 
			"        \"class\": \"boundary\",\r\n" + 
			"        \"type\": \"administrative\",\r\n" + 
			"        \"importance\": 0.882755579931374,\r\n" + 
			"        \"icon\": \"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\",\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"state\": \"Osaka Prefecture\",\r\n" + 
			"            \"country\": \"Japan\",\r\n" + 
			"            \"country_code\": \"jp\"\r\n" + 
			"        }\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 2486350,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"node\",\r\n" + 
			"        \"osm_id\": 346685291,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"34.6971912\",\r\n" + 
			"            \"34.7071912\",\r\n" + 
			"            \"135.4905866\",\r\n" + 
			"            \"135.5005866\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"34.7021912\",\r\n" + 
			"        \"lon\": \"135.4955866\",\r\n" + 
			"        \"display_name\": \"Osaka, South Concourse, Umeda 3, Kita Ward, Osaka, Osaka Prefecture, 530-0001, Japan\",\r\n" + 
			"        \"class\": \"railway\",\r\n" + 
			"        \"type\": \"station\",\r\n" + 
			"        \"importance\": 0.726310557311489,\r\n" + 
			"        \"icon\": \"https://nominatim.openstreetmap.org/images/mapicons/transport_train_station2.p.20.png\",\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"railway\": \"Osaka\",\r\n" + 
			"            \"road\": \"South Concourse\",\r\n" + 
			"            \"neighbourhood\": \"Umeda 3\",\r\n" + 
			"            \"suburb\": \"Kita Ward\",\r\n" + 
			"            \"city\": \"Osaka\",\r\n" + 
			"            \"province\": \"Osaka Prefecture\",\r\n" + 
			"            \"postcode\": \"530-0001\",\r\n" + 
			"            \"country\": \"Japan\",\r\n" + 
			"            \"country_code\": \"jp\"\r\n" + 
			"        }\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"        \"place_id\": 235221208,\r\n" + 
			"        \"licence\": \"Data (C) OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\r\n" + 
			"        \"osm_type\": \"relation\",\r\n" + 
			"        \"osm_id\": 358674,\r\n" + 
			"        \"boundingbox\": [\r\n" + 
			"            \"34.586154\",\r\n" + 
			"            \"34.768849\",\r\n" + 
			"            \"135.3099784\",\r\n" + 
			"            \"135.59935\"\r\n" + 
			"        ],\r\n" + 
			"        \"lat\": \"34.6937569\",\r\n" + 
			"        \"lon\": \"135.5014539\",\r\n" + 
			"        \"display_name\": \"Osaka, Osaka Prefecture, Japan\",\r\n" + 
			"        \"class\": \"boundary\",\r\n" + 
			"        \"type\": \"administrative\",\r\n" + 
			"        \"importance\": 0.6200000000000001,\r\n" + 
			"        \"icon\": \"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\",\r\n" + 
			"        \"address\": {\r\n" + 
			"            \"city\": \"Osaka\",\r\n" + 
			"            \"province\": \"Osaka Prefecture\",\r\n" + 
			"            \"country\": \"Japan\",\r\n" + 
			"            \"country_code\": \"jp\"\r\n" + 
			"        }\r\n" + 
			"    }\r\n" + 
			"]";
	
	private Location testLocation;
	private Location testLocation2;

    @BeforeEach
    void setup(){
        testLocation = Mockito.mock(Location.class);
		Mockito.when(testLocation.getCity()).thenReturn("Test City");
        Mockito.when(testLocation.getState()).thenReturn("Test State");
        Mockito.when(testLocation.getCountry()).thenReturn("Test Country");
        Mockito.when(testLocation.getLatitude()).thenReturn(null);
        Mockito.when(testLocation.getLongitude()).thenReturn(null);


        testLocation2 = Mockito.mock(Location.class);
        Mockito.when(testLocation2.getCity()).thenReturn("Test City");
        Mockito.when(testLocation2.getState()).thenReturn("");
        Mockito.when(testLocation2.getCountry()).thenReturn("Test Country");
        Mockito.when(testLocation2.getLatitude()).thenReturn(null);
        Mockito.when(testLocation2.getLongitude()).thenReturn(null);
    }
    
    @Test
    public void testLookupLocation_BlueskyScenario_GetsCorrectLocation() {
    	Mockito.doReturn(christchurchLocationQuery).when(testLocation).performLocationSearch("Test City, Test State, Test Country");
    	
    	Location lookedupLocation = testLocation.lookupAndValidate();
    	
    	Assert.assertTrue(lookedupLocation.getLatitude()-(-43.530955f) < 0.0001f);
    	Assert.assertTrue(lookedupLocation.getLongitude()-(172.6366455f) < 0.0001f);
    	Assert.assertEquals("Christchurch", lookedupLocation.getCity());
    	Assert.assertEquals("Canterbury", lookedupLocation.getState());
    	Assert.assertEquals("New Zealand", lookedupLocation.getCountry());
    }
    
    @Test
    public void testLookupLocation_SearchWithoutState_GetsCorrectLocation() {
    	Mockito.doReturn(christchurchLocationQuery).when(testLocation2).performLocationSearch("Test City, Test Country");
    	
    	Location lookedupLocation = testLocation2.lookupAndValidate();
    	
    	Assert.assertTrue(lookedupLocation.getLatitude()-(-43.530955f) < 0.0001f);
    	Assert.assertTrue(lookedupLocation.getLongitude()-(172.6366455f) < 0.0001f);
    	Assert.assertEquals("Christchurch", lookedupLocation.getCity());
    	Assert.assertEquals("Canterbury", lookedupLocation.getState());
    	Assert.assertEquals("New Zealand", lookedupLocation.getCountry());
    }
    
    @Test
    public void testLookupLocation_PlaceIsNotACity_ReturnsNull() {
    	Mockito.doReturn(rejectNonCities).when(testLocation).performLocationSearch("Test City, Test State, Test Country");
    	
    	Location lookedupLocation = testLocation.lookupAndValidate();
    	
    	Assert.assertNull(lookedupLocation);
    }
    
    @Test
    public void testLookupLocation_NotAPlaceOrCity_ReturnsNull() {
    	Mockito.doReturn("[]").when(testLocation).performLocationSearch("Test City, Test State, Test Country");
    	
    	Location lookedupLocation = testLocation.lookupAndValidate();
    	
    	Assert.assertNull(lookedupLocation);
    }
    
    @Test
    public void testLookupLocation_FirstOptionNotACity_GetsCorrectLocation() {
    	Mockito.doReturn(nonFirstOption).when(testLocation).performLocationSearch("Test City, Test State, Test Country");
    	
    	Location lookedupLocation = testLocation.lookupAndValidate();
    	
    	Assert.assertTrue(lookedupLocation.getLatitude()-(34.6937569f) < 0.0001f);
    	Assert.assertTrue(lookedupLocation.getLongitude()-(135.5014539f) < 0.0001f);
    	Assert.assertEquals("Osaka", lookedupLocation.getCity());
    	Assert.assertEquals("Osaka Prefecture", lookedupLocation.getState());
    	Assert.assertEquals("Japan", lookedupLocation.getCountry());
    }
}
