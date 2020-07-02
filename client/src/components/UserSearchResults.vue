<template>
  <div id="UserSearchResults">
    <v-data-table
    no-data-text="No users found"
    :headers="headers"
    :items="users"
    @click:row="viewUser"
    class="elevation-1"
    >
    <template #item.full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }}</template>
    <template v-slot:items="users">
      <td class="text-xs-right">{{ users.item.full_name }}</td>
      <td class="text-xs-right">{{ users.item.nickname }}</td>
      <td class="text-xs-right">{{ users.item.primary_email }}</td>
      <td class="text-xs-right">{{ users.item.activities }}</td>
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
  data () {
    return {
      headers: [
        { text: 'Name', value: 'full_name' },
        { text: 'Nickname', value: 'nickname' },
        { text: 'Email', value: 'primary_email' },
        { text: 'Interests', sortable: false, value: 'activities' }
      ],
      users: []
    }
  },
  created: function() {
    searchUsers("", "", "")
      .then((users) => {
        this.users = users;
      });
  },
  methods: {}
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
