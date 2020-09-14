<template>
  <div style="text-align: center" id="UserSearchResults">
  <p class="pl-1" style="color: red">{{ errorMessage }}</p>

  <v-col v-if="isAdmin" class="d-flex justify-left admin-controls">
      <div v-if="selectedUsers.length === 1">
        <v-btn color="primary" @click="editSelectedUser" class="mr-8">edit</v-btn>

        <v-dialog
      v-model="deleteUserModal"
      width="450"
    >
      <template v-slot:activator="{ on }">
        <v-btn
          color="red lighten-2"
          dark
          v-on="on"
          class="mr-8"
        >
          delete
        </v-btn>
      </template>

      <v-card>
        <v-card-title
          class="headline"
          primary-title
        >
          Delete {{selectedUsers[0].firstname + ' ' + selectedUsers[0].lastname}}?
        </v-card-title>

        <v-card-text>
          This will delete the user and all of their information. This operation cannot be undone.
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn
            text
            @click="deleteUserModal = false"
          >
            cancel
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="error"
            text
            @click="deleteSelectedUser"
          >
            delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


        <v-btn v-if="!selectedUserIsAdmin" color="success" @click="promoteSelectedUser" class="mr-8">promote</v-btn>
        <v-btn v-if="selectedUserIsAdmin" color="warning" @click="demoteSelectedUser" class="mr-8">demote</v-btn>
      </div>
  </v-col>
    <v-data-table
    :no-data-text="noDataText"
    :headers="headers"
    :items="activities"
    item-key="activity_id"
    @click:row="goToActivity"
    :page.sync="page"
    :show-select="isAdmin"
    single-select
    v-model="selectedUsers"
    >
    <template #item.full_name="{ item }">{{ item.activityName }} {{ item.creatorName }} {{ item.location }}</template>
    <template #item.short_interests="{ item }">{{getActivitiesString(item.activities)}}</template>
    <template v-slot:items="activities">
      <td class="text-xs-right">{{ activities.item.activityName }}</td>
      <td class="text-xs-right">{{ activities.item.short_interests }}</td>
      <td class="text-xs-right">{{ activities.item.creatorName }}</td>
      <td class="text-xs-right">{{ activities.item.location }}</td>
      <td class="text-xs-right">{{ activities.item.participants }}</td>
      <td class="text-xs-right">{{ activities.item.followers }}</td>
    </template>
  </v-data-table>

  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { searchActivities } from '../controllers/activitySearch.controller';
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "@/scripts/User";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '@/scripts/Activity'
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';
import * as auth from "../services/auth.service";
import * as adminController from "../controllers/admin.controller";
import { deleteUserAccount } from "../controllers/profile.controller"

// app Vue instance
const ActivitySearchResults = Vue.extend({
  name: "ActivitySearchResults",
  props: ['searchTerms'],
  watch: {
    searchTerms(newValue) {
      this.search(newValue);
    }
  },
  data () {
    return {
      headers: [
        { text: 'Name', value: 'activityName' },
        { text: 'Type', sortable: false, value: 'short_interests' },
        { text: 'Creator', value: 'creatorName' },
        { text: 'Location', value: 'location' },
        { text: 'Particpant Count', value: 'participants' },
        { text: 'Follower Count', value: 'followers'}
      ],
      isAdmin: false,
      activties: [] as CreateActivityRequest[],
      deleteUserModal: false,
      users: [] as UserApiFormat[],
      errorMessage: "",
      noDataText: "No users found",
      searchRulesModal: false,
      page: 1
    }
  },
  created: async function() {
    if (auth.isAdmin()) {
      this.isAdmin = true;
    }
    await this.search(this.searchTerms);
    this.checkPage();
  },

  computed: {

    selectedUser: function() {
      if (this.selectedUsers.length !== 1) {
        return null;
      }
      let selectedUser: UserApiFormat = this.selectedUsers[0];
      return selectedUser;
    },

    selectedUserIsAdmin: function() {
      if (this.selectedUsers.length !== 1) {
        return false;
      }

      let selectedUser: UserApiFormat = this.selectedUsers[0];
      return selectedUser.permission_level && selectedUser.permission_level >= 120;
    }
  },


  methods: {
    goToActivity: function(activityId: any) {
      this.$router.push(`/profiles/${this.creatorId}/activities/${activityId}`);
    },
    search: async function(searchTerms: Dictionary<string>) {
      this.noDataText = "No activities found";
      this.errorMessage = "";
      try {
        let activities = await searchActivities(searchTerms)
        this.activities = activities as CreateActivityRequest[];
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
        this.activities = [];
      }
    },

  },
});
    
export default ActivitySearchResults;
</script>

<style>
[v-cloak] {
  display: none;
}
p {
  display: inline-block;
}

.admin-controls {
  height: 50px;
}
</style>
