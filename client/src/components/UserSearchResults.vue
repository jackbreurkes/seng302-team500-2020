<template>
  <div style="text-align: center" id="UserSearchResults">
  <p class="pl-1" style="color: red">{{ errorMessage }}</p>

    <v-data-table
    :no-data-text="noDataText"
    :headers="headers"
    :items="users"
    class="elevation-1"
    @click:row="goToUser"
    :page.sync="page"
    >
    <template #item.full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }}</template>
    <!-- <template #item.full_name="{ item }">{{ item.firstname }} {{ item.userId }}{{ item.middlename }} {{ item.lastname }}</template> -->
    <template #item.short_interests="{ item }">{{getActivitiesString(item.activities)}}</template>
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
import { searchUsers, getShortenedActivitiesString } from '../controllers/userSearch.controller';
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
      searchRulesModal: false,
      page: 1
    }
  },
  created: async function() {
    await this.search(this.searchTerms);
    this.checkPage();
  },
  methods: {
    goToUser: function(userId: any) {
      if(this.page != 1){
        localStorage.setItem("searchPage", this.page.toString());
      }
      this.$router.push("/profiles/" + userId.profile_id);
    },
    checkPage: function(){
      if(localStorage.getItem("searchPage")){
        this.page = parseInt(localStorage.getItem("searchPage")!);
        console.log("What page we should we be on " + this.page);
        localStorage.removeItem("searchPage");
      }
    },
    getActivitiesString: function(activities : string[]){
      return getShortenedActivitiesString(activities, this.searchTerms)
    },
    search: async function(searchTerms: Dictionary<string>) {
      this.noDataText = "No users found";
      this.errorMessage = "";
      try {
        let users = await searchUsers(searchTerms)
        this.users = users as UserApiFormat[];
      } catch (err) {
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
      }
    }
  },
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
