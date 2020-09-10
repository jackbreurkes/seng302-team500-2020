package com.springvuegradle.model.data;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * JPA POJO to represent a location
 */
@Entity
@Table(name="location", uniqueConstraints={
        @UniqueConstraint(columnNames = {"city", "country"})
})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Location implements Serializable {

    /**
	 * Required randomly generated field
	 */
	private static final long serialVersionUID = 7638087353446385507L;

	@Id
    @GeneratedValue
    @JsonIgnore
    private long locationId;

    @NotNull
    private String city;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String state;

    @NotNull
    private String country;
    
    /**
     * Latitude of this city
     */
    private Float latitude;

	/**
     * Longitude of this city
     */
    private Float longitude;

    /**
     * Constructor required by Spring
     */
    protected Location() {}

    /**
     * constructor with required args
     * @param city the city
     * @param country the country the city is in
     */
    public Location(String city, String country) {
        this.city = city;
        this.country = country;
        this.state = null;
    }

    /**
     * constructor with optional state parameter
     * @param city the city
     * @param state optional state
     * @param country the country the city is in
     */
    public Location(String city, String state, String country) {
        this.city = city;
        this.state = state;
        this.country = country;
    }
    
    /**
     * constructor with coordinates and optional state parameter
     * @param city the city
     * @param state optional state
     * @param country the country the city is in
     * @param latitude the latitude of the marker for the center of the city
     * @param longitude the longitude of the marker for the center of the city
     */
    public Location(String city, String state, String country, float latitude, float longitude) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return unique ID for the location, required for retrieving from repository
     */
    public long getLocationId() {
        return locationId;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the optional state value, or null if not defined
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the optional state value
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the country the city is in
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country the city is in
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
	 * Looks up a location with the nominatim API then returns a location object
	 * which corresponds to the location as returned by the API request
	 * (this means the case is corrected, the language is changed to english and
	 * the state is added if not specified)
	 * @param this Location object to this
	 * @return Location as returned by the API, or null if the lookup failed
	 */
	public Location lookupAndValidate() {

		String locationString = null;
		if (this.getState() != null && this.getState() != "") {
			locationString = this.getCity() + ", " + this.getState()+ ", " + this.getCountry();
		} else {
			locationString = this.getCity() + ", " + this.getCountry();
		}
		
		
		if (locationString.contains("&") || locationString.contains("?")) {
			return null;
		}
		
		String result = makeRequest(locationString);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(result);
		
			if (rootNode instanceof ArrayNode)
			for (JsonNode node : ((ArrayNode)rootNode)) {
				if ((node.get("class").asText().equals("boundary") && node.get("type").asText().equals("administrative"))
						|| (node.get("class").asText().equals("place") && node.get("type").asText().equals("city"))) {
					JsonNode addressNode = node.get("address");
					String city = addressNode.has("city") ? addressNode.get("city").asText() :
						(addressNode.toString().split(Pattern.quote("\""))[3]);
					String state = addressNode.has("state") ? addressNode.get("state").asText() : 
						(addressNode.has("province") ? addressNode.get("province").asText() : null);
					String country = addressNode.get("country").asText();
					
					Location location = new Location(city, country);
					if (state != null) {
						location.setState(state);
					}
					location.setLatitude(node.get("lat").floatValue());
					location.setLongitude(node.get("lon").floatValue());
					return location;
				}
			}
		} catch (JsonProcessingException | NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String makeRequest(String locationString) {
		final String uri = "https://nominatim.openstreetmap.org/search/?q=" + locationString
				+ "&format=json&addressdetails=1&accept-language=en&limit=10";

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		
		return result;
	}
	
	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
}
