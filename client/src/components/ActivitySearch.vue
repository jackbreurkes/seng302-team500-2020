<template>
  <div style="text-align: center" id="Activities">
    <v-container fill-height align-content-center>
      <v-row justify="center">
        <v-col sm="12" md="12" lg="12">
          <v-card class="elevation-12">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Search Activities</v-toolbar-title>
            </v-toolbar>

            <v-container>
            <div class="mt-3 mx-2" fill-height fill-width>
              <v-col cs="12" sm="12" md="9" lg="9">
                <v-text-field label="Activity Name" outlined v-model="searchString" @keyup.enter.native="search()"></v-text-field>
              </v-col>

              <v-col md="12" class="d-flex justify-center">
                <v-btn x-large color="primary" v-on:click="searchClicked()">Search</v-btn>
              </v-col>
            </div>

            <v-row>
              <v-col sm="2" md="2" lg="1">
                <v-dialog v-model="searchRulesModal" width="400">
                  <template v-slot:activator="{ on }">
                    <v-btn v-on="on" color="info">View Search Rules</v-btn>
                  </template>

                  <v-card>
                    <v-card-title class="headline" primary-title>Search Rules</v-card-title>
                    <v-divider></v-divider>
                    <br />
                    <v-card-text>
                      <p>To do a complete match search you must use quotation marks</p>
                      <p>
                        e.g.
                        <strong>"Swimming in lake"</strong> will only match with activities with
                        <strong>Swimming in lake</strong> in the name.
                      </p>
                      <p>To do a partial match you only need to put spaces between the words</p>
                      <p>
                        e.g.
                        <strong>Swimming in lake</strong> will match with activities that have
                        <strong>Swimming</strong>,
                        <strong>in</strong> or
                        <strong>lake</strong> in the name
                      </p>
                    </v-card-text>

                    <v-divider></v-divider>

                    <v-card-actions>
                      <v-col class="text-right">
                        <v-btn class="ml-3" text @click="searchRulesModal = false">Close</v-btn>
                      </v-col>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </v-col>
            </v-row>
            <p class="pl-1" style="color: red">{{ errorMessage }}</p>
            <v-pagination v-model="pageNumber" :length="pageCount.count" @input="search()"></v-pagination>
            <div id="activitySearchResults">
              <ActivitySearchResults :results="searchResults" :pageSize="pageSize" :key="resultsKey"></ActivitySearchResults>
            </div>
            </v-container>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script lang="ts">
import Vue from "vue";
import ActivitySearchResults from "./ActivitySearchResults.vue";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from "../scripts/Activity";
// eslint-disable-next-line no-unused-vars
import { Dictionary } from "vue-router/types/router";
import * as activitySearch from "../controllers/activitySearch.controller";
import * as preferences from "../services/preferences.service";
import { fetchProfileWithId } from "../controllers/profile.controller"

// app Vue instance
const Activities = Vue.extend({
  name: "Activities",
  components: { ActivitySearchResults },

  // app initial state
  data: function() {
    return {
      searchRulesModal: false,
      headers: [
        { text: "Name", value: "activity_name" },
        { text: "Type", value: "activity_type" },
        { text: "Creator", value: "creator" },
        { text: "Location", value: "location" },
        { text: "Participant Count", value: "num_participants" },
        { text: "Follower Count", value: "num_followers" }
      ],
      searchString: "",
      searchResults: [] as (CreateActivityRequest & {creator_name? : string})[],
      creatorNames: {} as Dictionary<string>,
      pageNumber: NaN, // loaded in created hook
      pageSize: NaN, // loaded in created hook
      pageCount: {query: "", count: 0}, // used to keep track of how the paginator should be displayed
      errorMessage: "",
      resultsKey: 0
    };
  },

  created() {
    this.searchString = preferences.getSavedActivitySearchQuery();
    this.pageNumber = preferences.getSavedActivitySearchPage();
    this.pageSize = preferences.getPreferredSearchPageSize();
    if (this.searchString !== "") {
      this.search();
    }
  },

  methods: {
    /**
     * starts a search from at the first page.
     */
    searchClicked() {
      this.pageNumber = 1;
      this.search();
    },

    /**
     * sends a search request and updates the search results list accordingly.
     */
    search: async function() {
      this.errorMessage = "";
      try {
        this.searchResults = await activitySearch.searchActivities(
          this.searchString,
          this.pageNumber - 1,
          this.pageSize,
        );
        this.$root.$emit('mapShowSearchResults', this.searchResults);
      } catch (e) {
        this.searchResults = [];
        this.errorMessage = e.message;
      }
      preferences.saveActivitySearchPosition(this.searchString, this.pageNumber, this.pageSize);
      this.updateTotalPageCount();
      this.setActivityCreatorNames();
    },

    /**
     * used to update the paginator displayed below the results table.
     * if there are more pages after the current page, will add one page to the list of accessible pages in the paginator.
     * this means we do not have to get the total number of results returned by a search to know how many pages there are.
     */
    async updateTotalPageCount() {
      let previousQuery = this.pageCount.query;
      let previousPageCount = this.pageCount.count;
      if (this.searchString !== previousQuery) {
        this.pageCount = {
          query: this.searchString,
          count: this.pageNumber
        }
        previousQuery = this.searchString;
        previousPageCount = this.pageCount.count;
      }
      if (this.pageNumber < previousPageCount) {
        return;
      }
      let nextPageResults = [];
      try {
        nextPageResults = await activitySearch.searchActivities(
          this.searchString,
          this.pageNumber,
          this.pageSize,
        );
      } catch (e) {
        // do nothing
      }
      if (nextPageResults.length === 0) {
        return; // there is no next page
      }
      this.pageCount = {
        query: this.searchString,
        count: this.pageNumber + 1
      }
    },

    /**
     * sets the creator names for each activity in the search results for displaying in the table.
     */
    setActivityCreatorNames() {
      this.searchResults.forEach((result, index) => {
        if (!result.creator_id) {
          return;
        }
        const creatorId: number = result.creator_id;
        if (this.creatorNames[creatorId] === undefined) {
            this.searchResults[index].creator_name = "loading...";
            this.getCreatorName(creatorId).then(name => {
                this.creatorNames[creatorId] = name;
                this.searchResults[index].creator_name = this.creatorNames[creatorId];
                this.forceUpdateResultsTable();
            });
        }
        this.searchResults[index].creator_name = this.creatorNames[creatorId];
      });
      this.forceUpdateResultsTable();
    },

    /**
     * forces the results table to update.
     * updates using the Vue.js key attribute
     * https://michaelnthiessen.com/understanding-the-key-attribute/
     * to implement the key changing technique
     * https://michaelnthiessen.com/key-changing-technique/
     */
    forceUpdateResultsTable() {
        this.resultsKey += 1;
    },

    /**
     * retrieves the name of a creator as a string given their id.
     * @param creatorId the id of the creator whose name should be retrieved
     */
    async getCreatorName(creatorId: number) {
      const creatorProfile = await fetchProfileWithId(creatorId);
      let creatorName = creatorProfile.firstname + " ";
      creatorName += creatorProfile.middlename ? creatorProfile.middlename + " " : "";
      creatorName += creatorProfile.lastname;
      return creatorName;
    },
  }
});

export default Activities;
</script>

<style>
[v-cloak] {
  display: none;
}

.mandatory {
  background-color: #ccf;
}

p {
  display: inline-block;
}

#method-radios {
  min-width: 140px;
}

#activity-selector {
  min-width: 200px;
}
</style>
