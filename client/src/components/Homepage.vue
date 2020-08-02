<template>
  <div v-if="currentUser.profile_id">
    <div>
      <v-container fill-height align-content-center>
        <v-row justify="center">
          <v-col sm="12" md="10" lg="4">
            <v-card class="elevation-12">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>Profile: {{ currentUser.nickname ? currentUser.nickname : `${currentUser.firstname} ${currentUser.lastname}` }}</v-toolbar-title>
                <v-spacer></v-spacer>
                <div v-if="currentlyHasAuthority">
                  <v-menu bottom left offset-y>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        dark
                        icon
                        v-bind="attrs"
                        v-on="on"
                      >
                        <v-icon>mdi-dots-vertical</v-icon>
                      </v-btn>
                    </template>

                    <v-list>
                      <v-list-item
                        @click="editProfile"
                      >
                        <v-list-item-title>Edit profile</v-list-item-title>
                      </v-list-item>
                      <v-list-item @click="confirmDeleteModal = true">
                        <v-dialog v-model="confirmDeleteModal" width="290">
                          <template v-slot:activator="{ on }">
                            <v-list-item-title v-on="on">Delete account</v-list-item-title>
                          </template>
                          <v-card>
                            <v-card-title class="headline" primary-title>Delete account?</v-card-title>
                            <v-card-text>This operation cannot be undone.</v-card-text>
                            <v-divider></v-divider>
                            <v-card-actions>
                              <v-btn text @click="confirmDeleteModal = false">Cancel</v-btn>
                              <v-spacer></v-spacer>
                              <v-btn color="error" text @click="deleteAccount">Delete</v-btn>
                            </v-card-actions>
                          </v-card>
                        </v-dialog>
                      </v-list-item>
                    </v-list>
                  </v-menu>
                </div>
              </v-toolbar>
              <v-card-text class="grey lighten-4">
                <h3 id="myName">Name</h3>
                <p>{{ currentUser.firstname }} {{currentUser.middlename}} {{currentUser.lastname}} {{currentUser.nickname ? `(${currentUser.nickname})` : ""}}</p>
  
                <div v-if="currentUser.bio">
                  <h3>Bio</h3>
                  <p>{{ currentUser.bio }}</p>
                  <br />
                </div>
  
                <h3>Info</h3>
                <p>Born on {{ currentUser.date_of_birth }}</p>
                <br />
                <p>Identifies as {{ currentUser.gender }}</p>
                <br />
                <div v-if="currentUser.fitness !== undefined && currentUser.fitness != -1">
                  <p>Fitness level {{ currentUser.fitness }}</p>
                  <br />
                </div>
  
                <div v-if="currentUser.location !== undefined && currentUser.location.city !==null && currentUser.location.city.length != 0">
                <br />
                <p> Current Location: {{ currentUser.location.city }}, {{ currentUser.location.state }}, {{ currentUser.location.country }}</p>
                <br />
                </div>
  
                <div v-if="currentlyHasAuthority">
                  <p class="mb-0">Primary email: {{ currentUser.primary_email }}</p>
                  <div v-if="currentUser.additional_email && currentUser.additional_email.length > 0">
                    <p class="mb-0">Secondary Emails {{ currentUser.additional_email.length }} / 4:</p>
                    <ul>
                      <li v-for="email in currentUser.additional_email" :key="email">{{ email }}</li>
                    </ul>
                    <br />
                  </div>
                </div>
  
                <div v-if="currentUser.passports && currentUser.passports.length > 0">
                  <h3>Passport Countries</h3>
                  <v-chip
                    class="mr-1 mb-2"
                    v-for="country of currentUser.passports"
                    v-bind:key="country"
                    outlined
                  >{{ country }}</v-chip>
                </div>
  
              </v-card-text>
            </v-card>
          </v-col>
          <v-col sm="12" md="10" lg="8">
            <v-card class="elevation-12">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>{{`${currentUser.firstname} ${currentUser.lastname}`}}'s Activities</v-toolbar-title>
                <v-spacer></v-spacer>
                <div v-if="currentlyHasAuthority">
                <!--TODO Menu for this plus make sure admin knows this will create it on their behalf-->
                  <v-btn @click="createActivityClicked" outlined>Create Activity</v-btn>
                </div>
              </v-toolbar>
  
              <v-card-text class="grey lighten-4">
                <div v-if="currentUser.activities && currentUser.activities.length > 0">
                  <h3>Interests</h3>
                  <v-chip
                    class="mr-2 mb-2"
                    v-for="activityType of currentUser.activities"
                    v-bind:key="activityType"
                  >{{ activityType }}</v-chip>
                </div>
                <v-tabs v-model="durationTab" grow>
                  <v-tab v-for="item in activityList" :key="item.tab">{{ item.tab }}</v-tab>
                </v-tabs>
                <v-tabs-items v-model="durationTab">
                  <v-tab-item v-for="item in activityList" :key="item.tab">
                    <ActivitiesList :profileId="currentProfileId" :authority="currentlyHasAuthority" :activities="item.content"></ActivitiesList>
                  </v-tab-item>
                </v-tabs-items>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

import ActivitiesList from "./ActivitiesList.vue";
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "../scripts/User";
import {
  logoutCurrentUser,
  fetchProfileWithId,
  deleteUserAccount
} from "../controllers/profile.controller";
import FormValidator from "../scripts/FormValidator";
// eslint-disable-next-line no-unused-vars
import { RegisterFormData } from "../controllers/register.controller";
import { 
  getActivitiesByCreator,
  getDurationActivities,
  getContinuousActivities,
} from '../controllers/activity.controller';
import { removeAdminMode } from "../services/properties.service";
import { clearAuthInfo } from "../services/auth.service";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from "../scripts/Activity";
import * as PropertiesService from '../services/properties.service';
import * as authService from "../services/auth.service"

// app Vue instance
const Homepage = Vue.extend({
  name: "Homepage",
  components: { ActivitiesList },

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
    return {
      currentProfileId: NaN as number,
      currentUser: {} as UserApiFormat,
      currentlyHasAuthority: false as boolean,
      confirmDeleteModal: false,
      // newEmail: "",
      // email: "",
      editedUser: {} as UserApiFormat,
      formValidator: new FormValidator(),
      durationActivities: [] as CreateActivityRequest[],
      continuousActivities: [] as CreateActivityRequest[],
      durationTab: null,
      activityList: [] as any,
      inputRules: {
        lastnameRules: [
          (v: string) =>
            formValidator.checkLastnameValidity(v) ||
            formValidator.LAST_NAME_ERROR_STRING
        ],
        firstnameRules: [
          (v: string) =>
            formValidator.checkFirstnameValidity(v) ||
            formValidator.FIRST_NAME_ERROR_STRING
        ],
        middlenameRules: [
          (v: string) =>
            formValidator.checkMiddlenameValidity(v) ||
            formValidator.MIDDLE_NAME_ERROR_STRING
        ],
        nicknameRules: [
          (v: string) =>
            formValidator.checkNicknameValidity(v) ||
            formValidator.NICKNAME_ERROR_STRING
        ],
        bioRules: [
          (v: string) =>
            formValidator.checkBioValidity(v) || formValidator.BIO_ERROR_STRING
        ],
        dobRules: [
          (v: string) =>
            formValidator.checkDobValidity(v) || formValidator.DOB_ERROR_STRING
        ],
        genderRules: [
          (v: string) =>
            formValidator.checkGenderValidity(v) ||
            formValidator.GENDER_ERROR_STRING
        ]
      }
    };
  },

  created() {
    const profileId: number = parseInt(this.$route.params.profileId);
    if (isNaN(profileId)) {
      this.$router.push({ name: "login" });
    }
    this.currentProfileId = profileId;
    
    let myProfileId = authService.getMyUserId()
    if (myProfileId == profileId || PropertiesService.getAdminMode()) {
      this.currentlyHasAuthority = true;
    }

    fetchProfileWithId(profileId)
      .then(user => {
        this.currentUser = user;
      })
      .catch(err => {
        if (err.response && err.response.status === 401) {
          this.$router.push({ name: "login" });
        } else if (err.response && err.response.status === 404) {
          this.$router.push({ name: "landing" });
        }
        console.error(err);
      });

    getActivitiesByCreator(profileId)
      .then(res => {
        this.durationActivities = getDurationActivities(res);
        this.continuousActivities = getContinuousActivities(res);
        this.activityList.push({tab: "Duration", content: this.durationActivities});
        this.activityList.push({tab: "Continuous", content: this.continuousActivities});
        })
      .catch(err => {
        console.error(err);
      });
  },

  methods: {
    //click logout button
    logoutButtonClicked: function() {
      logoutCurrentUser()
        .then(() => {
          this.$router.push({ name: "login" });
        })
        .catch((err: any) => {
          console.error(err);
          this.$router.push({ name: "login" });
        });
    },

    editProfile: function() {
      this.$router.push(`/profiles/${this.currentProfileId}/edit`);
    },

    createActivityClicked: function() {
      this.$router.push(`/profiles/${this.currentProfileId}/createActivity`);
    },

    /**
     * Delete the account of the user currently being looked at
     * Can only be done by an admin or the user themself
     */
    deleteAccount: function() {
        deleteUserAccount(this.currentProfileId)
        .then(() => {
          if (authService.getMyUserId() == this.currentProfileId) {
            //if we're editing ourself
            removeAdminMode();
            clearAuthInfo();  
            this.$router.push({ name: "register" });  
          } else {
            this.$router.push({ name: "adminDashboard" });
          }
        })
        .catch((err) => {
          if (!err.response || !err.response.status) {
            console.log(err)
          } else {
            console.log(err.response.status)
            // May wish to eventually take different actions depending on type of error returned from server (once implemented)
            // E.g. If the server decides to require a password and returns a 400 when password is wrong
            // I.e. Basically, warn the user if the type of error means that the account has not actually been deleted
          }
        })
    }
  }
});

export default Homepage;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
