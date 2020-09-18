<template>
  <div  style="text-align: center" id="Search">
        <v-container fill-height align-content-center>
            <v-row justify="center">
            <v-col sm="12" md="12" lg="12">
            <v-card class="elevation-12">
                <v-toolbar color="primary" dark flat>
                    <v-toolbar-title>Search Users</v-toolbar-title>
                </v-toolbar>
            

            <div id="searchAndFilter" class="mt-3 mx-2" fill-height fill-width>
              <v-row class="d-flex align-center">

                <v-col xs="12" sm="12" md="9" lg="9" v-if="searchBy == 'Name'">
                  <v-row class= "align-center">
                  <v-col xs="12" sm="12" md="4" lg="4">
                  <v-text-field
                    label="Search users by First Name"
                    :placeholder="searchBy"
                    outlined
                    click:append="search('k')"
                    v-model="firstname"
                    @keyup.enter.native="search()"
                  ></v-text-field> </v-col>
                    <v-col xs="12" sm="12" md="4" lg="4">
                    <v-text-field
                    label="Search users by Middle Name"
                    :placeholder="searchBy"
                    outlined
                    click:append="search('k')"
                    v-model="middlename"
                    @keyup.enter.native="search()"
                  ></v-text-field> </v-col>
                    <v-col xs="12" sm="12" md="4" lg="4">
                    <v-text-field
                    label="Search users by Last Name"
                    :placeholder="searchBy"
                    outlined
                    click:append="search('k')"
                    v-model="lastname"
                    @keyup.enter.native="search()"
                  ></v-text-field> </v-col> </v-row>
                </v-col>
                <v-col xs="12" sm="12" md="9" lg="9" v-else-if="searchBy == 'Interests'">
                  <v-row class="px-5 d-flex flex-nowrap align-center">
                      <v-radio-group v-model="methodRadioGroup" class="pr-5" id="method-radios">
                        <v-radio label="at least one of" value="or"></v-radio>
                        <v-radio label="all of" value="and"></v-radio>
                      </v-radio-group>
                      <v-autocomplete
                        id="activity-selector"
                        :items="availableActivityTypes"
                        color="white"
                        item-text="name"
                        label="Interests"
                        placeholder="Select interests"
                        v-model="selectedActivityTypes"
                        chips
                        deletable-chips
                        multiple
                      ></v-autocomplete>
                  </v-row>
                </v-col>
                <v-col xs="12" sm="12" md="9" lg="9" v-else>
                  <v-text-field
                    label="Search users"
                    :placeholder="searchBy"
                    outlined
                    click:append="search('k')"
                    v-model="searchTerm"
                    @keyup.enter.native="search()"
                  ></v-text-field>
                </v-col>
                <v-col xs="12" sm="12" md="3" lg="3">
                  <v-select
                    :items="possibleSearchBys"
                    label="Search By:"
                    outlined
                    v-model="searchBy"
                  ></v-select>
                </v-col>
                <v-col md="12" class="d-flex justify-center">
                  <v-btn x-large color="primary" v-on:click="search()">Search</v-btn>
                </v-col>
              </v-row>

              <v-row>
                <v-col sm="2" md="2" lg="1">
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
                    </v-col>
                    </v-row>

                </div>
                <p class="pl-1" style="color: red">{{ errorMessage }}</p>
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
import FormValidator from "../scripts/FormValidator";
import UserSearchResults from "./UserSearchResults.vue";
import * as activityController from "../controllers/activity.controller";
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

// app Vue instance
const Search = Vue.extend({
  name: "Search",
  components: { UserSearchResults },

  // app initial state
  data: function() {
    //let formValidator = new FormValidator();
    return {
      possibleSearchBys: ["Name", "Nickname", "Email", "Interests"],
      searchBy: "Name",
      methodRadioGroup: "or",
      searchTerm: "",
      firstname: "",
      middlename: "",
      lastname: "",
      searchTerms: {} as Dictionary<string>,
      availableActivityTypes: [] as string[],
      selectedActivityTypes: [] as string[],
      searchRulesModal: false,
        errorMessage: "",
    };
  },

  created() {
    activityController
      .getAvailableActivityTypes()
      .then(activityTypes => {
        this.availableActivityTypes = activityTypes;
      })
      .catch(err => {
        console.error("unable to load activity types");
        console.error(err);
      });
      this.checkSaved();
  },

  methods: {
      search: function() {
          if (this.searchTerm.trim().length >= 1 ||
                (this.firstname.trim().length >= 1 || this.lastname.trim().length >= 1) ||
                (this.searchBy == "Interests" && this.selectedActivityTypes.length != 0)
          ) {
            if (this.searchBy == "Name") {
              if (this.firstname == "" && this.lastname == "") {
                this.errorMessage = "Must enter characters in at least either first or last name to search for.";
              } else {
                this.errorMessage = "";
                this.searchTerms = {"firstname": this.firstname, "middlename": this.middlename, "lastname": this.lastname};
              }
            } else if (this.searchBy == "Interests") {
              this.errorMessage = "";
              let searchActivitiesString = "";
              for (let activity in this.selectedActivityTypes) {
                searchActivitiesString += this.selectedActivityTypes[activity] + " ";
              }
              this.searchTerms = {"activity": searchActivitiesString.trim(), "method": this.methodRadioGroup}
            } else {
              this.errorMessage = "";
              this.searchTerms = {};
              this.searchTerms[this.searchBy.toLowerCase()] = this.searchTerm;
            }

          }  else {
            if (this.searchBy == "Name") {
              this.errorMessage = "Must enter at least 1 character for at least either first or last name";
            } else if (this.searchBy == "Interests") {
              this.errorMessage = "Must select at least 1 activity";
            } else {
              this.errorMessage = "Must enter a search string";
            }
          }
      },
      checkSaved: function(){
        if(localStorage.getItem("searchBy") != undefined){
          //TODO here check the timer
          this.searchBy = localStorage.getItem("searchBy")!;
          if(this.searchBy === "Name"){
            this.firstname = localStorage.getItem("searchFirstName")!;
            this.lastname = localStorage.getItem("searchLastName")!;
            this.middlename = localStorage.getItem("searchMiddleName")!;

            localStorage.removeItem("searchFirstName");
            localStorage.removeItem("searchLastName");
            localStorage.removeItem("searchMiddleName");

            this.searchTerms = {"firstname": this.firstname, "middlename": this.middlename, "lastname": this.lastname};
          }else if(this.searchBy === "Nickname"){
            this.searchTerm = localStorage.getItem("searchNickName")!;

            localStorage.removeItem("searchNickName");

            this.searchTerms[this.searchBy.toLowerCase()] = this.searchTerm;
          }else if(this.searchBy === "Email"){
            this.searchTerm = localStorage.getItem("searchEmail")!;

            localStorage.removeItem("searchEmail");
          
            this.searchTerms[this.searchBy.toLowerCase()] = this.searchTerm;
          }else{
            this.selectedActivityTypes = localStorage.getItem("searchActivityTypes")!.split(",");
            localStorage.removeItem("searchActivityTypes");

            let searchActivitiesString = "";
              for (let activity in this.selectedActivityTypes) {
                searchActivitiesString += this.selectedActivityTypes[activity] + " ";
              }
              this.searchTerms = {"activity": searchActivitiesString.trim(), "method": this.methodRadioGroup}
          }
          localStorage.removeItem("searchBy");
          
        }
      },

      prepareToExit(to: any) {
        if(to.name === "profilePage" && to.params.profileId != localStorage.getItem("userId")){
          //save to local storage
          localStorage.setItem("searchBy", this.searchBy);
          if(this.searchBy === "Email"){
            localStorage.setItem("searchEmail", this.searchTerm)
          }else if (this.searchBy === "Name"){
            localStorage.setItem("searchFirstName", this.firstname);
            localStorage.setItem("searchMiddleName", this.middlename);
            localStorage.setItem("searchLastName", this.lastname);
          }else if (this.searchBy === "Nickname"){
            localStorage.setItem("searchNickName", this.searchTerm)
          }else{
            //interests
            localStorage.setItem("searchActivityTypes", this.selectedActivityTypes.toString());
          }
        }
      }
    },
  beforeRouteLeave (to, from, next){
    this.prepareToExit(to);
    next()
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

#method-radios {
   min-width: 140px;
}

#activity-selector {
  min-width: 200px;
}
</style>
