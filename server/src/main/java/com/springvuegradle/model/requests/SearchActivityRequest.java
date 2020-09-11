package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchActivityRequest {
    @JsonProperty("searchTerms")
    private List<String> searchTerms;

    public SearchActivityRequest() {};

    public SearchActivityRequest(List<String> searchedTerms){
        this.searchTerms = searchedTerms;
    }

    public List<String> getSearchTerms() {
        return searchTerms;
    }

    public void setSearchedTerms(List<String> searchedTerms) {
        this.searchTerms = searchedTerms;
    }
}
