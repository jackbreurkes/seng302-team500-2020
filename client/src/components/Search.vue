<template>
  <div id="Search">
        <v-container fill-height align-content-center>
            <v-row align="center" justify="center">
            <v-col sm="12" md="12" lg="12">
            <v-card class="elevation-12">
                <v-toolbar color="primary" dark flat>
                    <v-toolbar-title>Search Users</v-toolbar-title>
                </v-toolbar>
            

                <div id="searchAndFilter" class="mt-3 mx-2" fill-height fill-width>
                    <v-row justify="center">
                    <v-col sm="12" md="12" lg="8">
                    <v-text-field
                        label="Search users"
                        :placeholder=searchBy
                        outlined
                        click:append="search('k')"
                        v-model="searchTerm"
                        @keyup.enter.native="search()"
                    ></v-text-field>
                    </v-col>
                    <v-col sm="6" md="4" lg="2">
                    <v-select
                        :items="possibleSearchBys"
                        label="Search By:"
                        outlined
                        v-model="searchBy"
                    ></v-select>
                    </v-col>
                    <v-col sm="4" md="4" lg="1">
                    <v-btn v-on:click="search()">Search</v-btn>
                    </v-col>
                    <!-- <v-col sm="2" md="2" lg="1">
                      <v-dialog v-model="searchRulesModal" width="400">
      <template v-slot:activator="{ on }">
        <v-btn v-on="on" color="info">View Search Rules</v-btn>
      </template>

      <v-card>
        <v-card-title class="headline" primary-title>Search rules</v-card-title>
        <v-divider></v-divider><br>
        <v-card-text>
          <p>None of the searches are case-sensitive.</p>
          <h4>
            Search by full name:
          </h4>
          <p>
            At least the user's first and last names must be given and each of these search terms must be at least 1 character.<br>
            The search terms given are matched to the beginning of each of the user's names. E.g. "be" would match "ben" but not "abel".<br>
            The middle name is not required, but can be given.
          </p>
          <h4>
            Search by nickname:
          </h4>
          <p>The search string matches from the start of the user's nickname with the minimum length of the search term being 1 character.</p>
          <h4>
            Search by email:
          </h4>
          <p>The search term matches from the beginning of the email and must contain at least all of the symbols up to (but not necessarily including the '@' symbol).</p>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-col class="text-right">
            <v-btn text @click="searchRulesModal = false">Close</v-btn>
          </v-col>
        </v-card-actions>
      </v-card>
    </v-dialog>
                    </v-col> -->
                    </v-row>

                </div>
                <div id="searchResults">
                    <UserSearchResults :searchTerms="searchTerms"></UserSearchResults>
                </div>
            </v-card>
            </v-col>
            </v-row>

        </v-container>
  </div>
</template>


<script lang="ts">
import Vue from "vue";
// eslint-disable-next-line no-unused-vars
import UserSearchResults from "./UserSearchResults.vue";

// app Vue instance
const Search = Vue.extend({
  name: "Search",
  components: { UserSearchResults },

  // app initial state
  data: function() {
    return {
        possibleSearchBys: ["Name", "Nickname", "Email"],
        searchBy: "Name",
        searchTerm: "",
        searchTerms: ["", "", ""],
        searchRulesModal: false
    };
  },

  created() {
      
  },

  methods: {
      search: function() {
          if (this.searchBy == this.possibleSearchBys[1]) {
            this.searchTerms = ["", this.searchTerm, ""];
          } else if (this.searchBy == this.possibleSearchBys[2]) {
            this.searchTerms = ["", "", this.searchTerm];
          } else {
            this.searchTerms = [this.searchTerm, "", ""];
          }
      }
  }
});

export default Search;
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
</style>
