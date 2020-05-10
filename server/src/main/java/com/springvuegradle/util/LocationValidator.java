package com.springvuegradle.util;

import java.util.regex.Pattern;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springvuegradle.model.data.Location;

public class LocationValidator {

	public static Location lookupAndValidate(Location query) {

		String locationString = null;
		if (query.getState() != null && query.getState() != "") {
			locationString = query.getCity() + ", " + query.getState()+ ", " + query.getCountry();
		} else {
			locationString = query.getCity() + ", " + query.getCountry();
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
					return location;
				}
			}
		} catch (JsonProcessingException | NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String makeRequest(String locationString) {
		final String uri = "https://nominatim.openstreetmap.org/search/?q=" + locationString
				+ "&format=json&addressdetails=1&accept-language=en&limit=3";

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		
		return result;
	}
}
