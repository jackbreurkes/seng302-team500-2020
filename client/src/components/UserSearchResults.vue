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
    onmouseover="" style="cursor: pointer;"
    :no-data-text="noDataText"
    :headers="headers"
    :items="users"
    item-key="profile_id"
    @click:row="goToUser"
    :page.sync="page"
    :show-select="isAdmin"
    single-select
    v-model="selectedUsers"
    >
    <template #item.full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }}</template>
    <template #item.short_interests="{ item }">{{getActivitiesString(item.activities)}}</template>
    <template v-slot:items="users">
      <td class="text-xs-right">{{ users.item.firstname }}</td>
      <td class="text-xs-right">{{ users.item.middlename }}</td>
      <td class="text-xs-right">{{ users.item.lastname }}</td>
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
import * as auth from "../services/auth.service";
import * as adminController from "../controllers/admin.controller";
import { deleteUserAccount } from "../controllers/profile.controller"

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
        { text: 'First Name', value: 'firstname' },
        { text: 'Middle Name', value: 'middlename' },
        { text: 'Last Name', value: 'lastname' },
        { text: 'Nickname', value: 'nickname' },
        { text: 'Email', value: 'primary_email' },
        { text: 'Interests', sortable: false, value: 'short_interests' }
      ],
      isAdmin: false,
      selectedUsers: [] as UserApiFormat[],
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
    goToUser: function(userId: any) {
      if(this.page != 1){
        localStorage.setItem("searchPage", this.page.toString());
      }
      this.$router.push("/profiles/" + userId.profile_id);
    },
    checkPage: function(){
      if(localStorage.getItem("searchPage")){
        this.page = parseInt(localStorage.getItem("searchPage")!);
        localStorage.removeItem("searchPage");
      }
    },
    getActivitiesString: function(activities : string[]){
      return getShortenedActivitiesString(activities, this.searchTerms)
    },
    search: async function(searchTerms: Dictionary<string>) {
      this.noDataText = "No users found. If you are unsure how to start, try reading the search rules";
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
    },

    /**
     * deletes the selected user and updates the view accordingly
     */
    deleteSelectedUser: function() {
      this.deleteUserModal = false;
      if (this.selectedUser === null) {
        return;
      }
      let deleteId: number = this.selectedUser.profile_id!;
      deleteUserAccount(deleteId)
        .then(() => {
          if (auth.getMyUserId() == deleteId) {
            auth.clearAuthInfo();  
            this.$router.push({ name: "register" });  
          } else {
            this.users = this.users.filter(user => user.profile_id !== deleteId);
          }
        })
        .catch((err) => {
          this.errorMessage = err.message;
        })
    },

    /**
     * takes the user to the edit page for the selected profile
     */
    editSelectedUser: function() {
      if (this.selectedUser === null) {
        return;
      }
      this.$router.push(`/profiles/${this.selectedUser.profile_id!}/edit`);
    },

    /**
     * promotes the selected user to an admin
     */
    promoteSelectedUser: async function() {
      if (this.selectedUser === null) {
        return;
      }
      try {
        await adminController.promoteUserToAdmin(this.selectedUser.profile_id!);
        this.selectedUser.permission_level = 126;
      } catch (e) {
        this.errorMessage = e.message;
      }
    },

    /**
     * demotes the selected admin user to a regular user
     */
    demoteSelectedUser: async function() {
      if (this.selectedUser === null) {
        return;
      }
      try {
        await adminController.demoteUserToBasicUser(this.selectedUser.profile_id!);
        this.selectedUser.permission_level = 0;
      } catch (e) {
        this.errorMessage = e.message;
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

.admin-controls {
  height: 50px;
}
</style>
