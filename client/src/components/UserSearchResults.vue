<template>
  <div id="UserSearchResults">
    <v-data-table
    no-data-text="No users found"
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

// app Vue instance
const UserSearchResults = Vue.extend({
  name: "UserSearchResults",
  props: ['searchTerms'],
  watch: {
    searchTerms(newValue, oldValue) {
      console.log(oldValue)
      console.log(newValue)
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
      users: []
    }
  },
  created: function() {
    this.search("", "", "");
  },
  methods: {
    getShortenedActivitiesString: function(activities: string[]) {
      let activitiesString = "";
      if (activities == undefined) {
        return "";
      } else if (activities.length <= 3) {
        for (let activity in activities) {
          activitiesString = activitiesString + activity + " ";
        }
        activitiesString.trim();
        return activitiesString;
      } else {
        for (let i = 0; i < 3; i++) {
          activitiesString = activitiesString + activities[i] + " ";
        }
        activitiesString.trim();
        return activitiesString+"...";
      }
    },
    search: function(name: string, nickname: string, email: string) {
      searchUsers(name, nickname, email)
      .then((users) => {
        this.users = users;
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
