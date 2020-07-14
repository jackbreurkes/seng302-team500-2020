<template>
  <div style="text-align: center" id="UserSearchResults">
    <p class="pl-1" style="color: red">{{ errorMessage }}</p>

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

    <v-data-table
    :no-data-text="noDataText"
    :headers="headers"
    :items="users"
    class="elevation-1"
    >
    <template #item.full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }}</template>
    <template #item.short_interests="{ item }">{{getShortenedActivitiesString(item.activities)}}</template>
    <template v-slot:items="users">
      <td class="text-xs-right">{{ users.item.full_name }}</td>
      <td class="text-xs-right">{{ users.item.nickname }}</td>
      <td class="text-xs-right">{{ users.item.primary_email }}</td>
      <td class="text-xs-right">{{ users.item.short_interests }}</td>
    </template>
  </v-data-table>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { searchUsers } from '../controllers/userSearch.controller';
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "@/scripts/User";

// app Vue instance
const UserSearchResults = Vue.extend({
  name: "UserSearchResults",
  props: ['searchTerms'],
  watch: {
    searchTerms(newValue) {
      this.search(newValue[0], newValue[1], newValue[2]);
    }
  },
  data () {
    return {
      headers: [
        { text: 'Name', value: 'full_name' },
        { text: 'Nickname', value: 'nickname' },
        { text: 'Email', value: 'primary_email' },
        { text: 'Interests', sortable: false, value: 'short_interests' }
      ],
      users: [] as UserApiFormat[],
      errorMessage: "",
      noDataText: "No users found",
      searchRulesModal: false
    }
  },
  created: function() {
    this.search("", "", "");
  },
  methods: {
    getShortenedActivitiesString: function(activities: string[]) {
      let activitiesString = "";
      if (activities == undefined || activities.length == 0) {
        return "";
      } else if (activities.length <= 3) {
        for (let activityIndex in activities) {
          activitiesString = activitiesString + activities[activityIndex] + ", ";
        }
        activitiesString.trim();
        activitiesString = activitiesString.substring(0, activitiesString.length-2);
        return activitiesString;
      } else {
        for (let i = 0; i < 3; i++) {
          activitiesString = activitiesString + activities[i] + ", ";
        }
        activitiesString.trim();
        activitiesString = activitiesString.substring(0, activitiesString.length-2);
        return activitiesString+"...";
      }
    },
    search: function(name: string, nickname: string, email: string) {
      this.noDataText = "No users found";
      this.errorMessage = "";
      searchUsers(name, nickname, email)
      .then((users) => {
        this.users = users as UserApiFormat[];
      })
      .catch((err) => {
        if (err.response) {
          if (err.response.status == 400) {
            this.errorMessage = err.message;
          }
        } else if (err.message) {
            this.errorMessage = err.message;
        } else {
            this.errorMessage = "Unexpected error";
        }
        this.noDataText = this.errorMessage;
        this.users = [];
      });
    }
  }
});
    
export default UserSearchResults;
</script>

<style>
[v-cloak] {
  display: none;
}
p {
  display: inline-block;
}
</style>
