<template>
  <div style="text-align: center" id="Activities">
    <v-container fill-height align-content-center>
      <v-row justify="center">
        <v-col sm="12" md="12" lg="12">
          <v-card class="elevation-12">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Search Activities</v-toolbar-title>
            </v-toolbar>

            <div class="mt-3 mx-2" fill-height fill-width>
              <v-col cs="12" sm="12" md="9" lg="9">
                <v-text-field label="Activity Name" outlined v-model="searchString"></v-text-field>
              </v-col>

              <v-col md="12" class="d-flex justify-center">
                <v-btn x-large color="primary" v-on:click="search()">Search</v-btn>
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
                        <v-btn text @click="searchRulesModal = false">Close</v-btn>
                      </v-col>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </v-col>
            </v-row>
            <p class="pl-1" style="color: red">{{ errorMessage }}</p>
            <div id="activitySearchResults">
              <ActivitySearchResults :results="searchResults"></ActivitySearchResults>
            </div>
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
import { CreateActivityRequest } from "@/scripts/Activity";
// eslint-disable-next-line no-unused-vars
import { Dictionary } from "vue-router/types/router";
import * as activitySearch from "../controllers/activitySearch.controller";
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
      searchTerms: [] as string[],
      searchResults: [] as (CreateActivityRequest & {creator_name : string})[],
      creatorNames: {} as Dictionary<string>,
      pageNumber: 0,
      errorMessage: ""
    };
  },

  created() {
    this.searchTerms = [];
  },

  methods: {
    /**
     * sends a search request and updates the search results list accordingly.
     */
    search: async function() {
      this.errorMessage = "";
      this.searchTerms = activitySearch.getSearchArguments(this.searchString);
      try {
        this.searchResults = await activitySearch.searchActivities(
          this.searchTerms
        );
      } catch (e) {
        this.searchResults = []
        this.errorMessage = e.message;
      }
      this.setActivityCreatorNames();
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
                this.forceUpdateResultsList();
            });
        }
        this.searchResults[index].creator_name = this.creatorNames[creatorId];
      });
      this.forceUpdateResultsList();
    },

    /**
     * forces the results list to update, thereby updating the data in the results table.
     */
    forceUpdateResultsList() {
        this.searchResults.push({} as CreateActivityRequest & {creator_name : string});
        this.searchResults.pop();
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
