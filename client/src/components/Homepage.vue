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
                        id="profileDropDown"
                      >
                        <v-icon>mdi-dots-vertical</v-icon>
                      </v-btn>
                    </template>

                    <v-list>
                      <v-list-item
                        id="editButton"
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
                  <p>Fitness level: {{ this.fitnessLevels[this.currentUser.fitness] }}</p>
                  <br />
                </div>
  
                <div v-if="currentUser.location !== undefined && currentUser.location.city !==null && currentUser.location.city.length != 0">
                <br />
                <p v-if="currentUser.location.state !== undefined"> Current Location: {{ currentUser.location.city }}, {{ currentUser.location.state }}, {{ currentUser.location.country }}</p>
                <p v-if="currentUser.location.state === undefined"> Current Location: {{ currentUser.location.city }}, {{ currentUser.location.country }}</p>
                <br />
                </div>
                   <p v-if="currentUser.location === undefined && currentUser.activities.length === 0">
                     Did you know you can get recommended activities by adding <br> a location and interests to your profile? <router-link to="" v-on:click.native="editProfile"> Click Here! </router-link>
                  </p>
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
                  <v-btn @click="createActivityClicked" outlined>Create Activity {{editingAsAdmin ? "as " + currentUser.firstname : ""}}</v-btn>
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
                  <v-tab v-for="item in activityList" :key="item.tab" :id= item.tab >{{ item.tab}}</v-tab>
                </v-tabs>
                <v-tabs-items v-model="durationTab">
                  <v-tab-item v-for="item in activityList" :key="item.tab">
                    <ActivitiesList :authority="currentlyHasAuthority" :activities="item.content" :tabState="item.tab"></ActivitiesList>
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
import { clearAuthInfo } from "../services/auth.service";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from "../scripts/Activity";
import * as authService from "../services/auth.service"

// app Vue instance
const Homepage = Vue.extend({
  name: "Homepage",
  components: { ActivitiesList },

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
    return {
      idOfDisplayedUser: NaN as number, // the ID of the profile the page is displaying
      currentUser: {} as UserApiFormat,
      currentlyHasAuthority: false as boolean,
      editingAsAdmin: false as boolean,
      confirmDeleteModal: false,
      // newEmail: "",
      // email: "",
      editedUser: {} as UserApiFormat,
      fitnessLevels: ["No Fitness", "A Little Fit", "Moderately Fit", "Enthusiastically Fit", "Crazy Fit"],
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
    this.idOfDisplayedUser = profileId;
    
    let myProfileId = authService.getMyUserId()
    if (myProfileId == profileId) {
      this.currentlyHasAuthority = true;
    } else if (authService.isAdmin()) {
      this.currentlyHasAuthority = true;
      this.editingAsAdmin = true;
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

  /**
   * handles the behaviour when the user navigates to another route that uses this component.
   * will refresh the page to load the new information.
   */
  beforeRouteUpdate(to, _, next) {
    const profileId: number = parseInt(to.params.profileId);
    if (isNaN(profileId)) {
      console.error(to.params.profileId + " is not a valid profile id")
      return
    }
    next()
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
      this.$router.push(`/profiles/${this.idOfDisplayedUser}/edit`);
    },

    createActivityClicked: function() {
      this.$router.push(`/profiles/${this.idOfDisplayedUser}/createActivity`);
    },

    /**
     * Delete the account of the user currently being looked at
     * Can only be done by an admin or the user themself
     */
    deleteAccount: function() {
        deleteUserAccount(this.idOfDisplayedUser)
        .then(() => {
          if (authService.getMyUserId() == this.idOfDisplayedUser) {
            //if we're editing ourself
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
