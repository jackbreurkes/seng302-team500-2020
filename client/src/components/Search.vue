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
                        append-icon="S"
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
                    <v-col sm="4" md="4" lg="2">
                    <v-btn v-on:click="search('pizza')">
              Searchie</v-btn>
                    </v-col>
                    </v-row>

                </div>
                <div id="searchResults">
                    <UserSearchResults :searchTerms="searchTerms"></UserSearchResults>
                </div>
            </v-card>
            </v-col>
            </v-row>
            <v-btn v-on:click="search('pizza')">
              Searchie</v-btn>

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
        searchTerms: ["", "", ""]
    };
  },

  created() {
      
  },

  methods: {
      search: function(string: string) {
          console.log("search'n for " + string);
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
