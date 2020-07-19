<template>
  <div style="text-align: center" id="UserSearchResults">
  <p class="pl-1" style="color: red">{{ errorMessage }}</p>

    <v-data-table
    :no-data-text="noDataText"
    :headers="headers"
    :items="users"
    class="elevation-1"
    @click:row="goToUser"
    
    >
    <template #item.full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }}</template>
    <!-- <template #item.full_name="{ item }">{{ item.firstname }} {{ item.userId }}{{ item.middlename }} {{ item.lastname }}</template> -->
    <template #item.short_interests="{ item }">{{getShortenedActivitiesString(item.activities)}}</template>
    <template v-slot:items="users">
      <!-- <td class="text-xs-right">{{ users.item.full_name }}</td> -->
      <td class="text-xs-right">{{ users.item.firstname }}</td>
      <td class="text-xs-right">{{ users.item.middlename }}</td>
      <td class="text-xs-right">{{ users.item.lastname }}</td>
      <!-- <td class="text-xs-right">{{ users.item.userId }}</td> -->
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
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

// app Vue instance
const UserSearchResults = Vue.extend({
  name: "UserSearchResults",
  props: ['searchTerms'],
  watch: {
    searchTerms(newValue) {
      this.search(newValue);
    }
  },
  data () {
    return {
      headers: [
        // { text: 'Name', value: 'full_name' },
        { text: 'First Name', value: 'firstname' },
        { text: 'Middle Name', value: 'middlename' },
        { text: 'Last Name', value: 'lastname' },
        //{ text: 'User Id', value: 'profile_id'},
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
    this.search(this.searchTerms);
  },
  methods: {
    goToUser: function(userId: any) {
      this.$router.push("/profiles/" + userId.profile_id);
    },
    getShortenedActivitiesString: function(activities: string[]) {
      let activitiesString = "";
      //here not working
      console.log(this.searchTerms["activity"]);
      if(this.searchTerms["activity"] !== undefined){
        return this.searchTerms["activity"].replace(/[ ,]+/g, ", ");
      }else{
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
      }
    },
    search: function(searchTerms: Dictionary<string>) {
      this.noDataText = "No users found";
      this.errorMessage = "";
      searchUsers(searchTerms)
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
