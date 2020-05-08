<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="top" justify="center">
        <v-col sm="12" md="8" lg="4">
          <v-card class="elevation-12">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Profile: {{ currentUser.nickname ? currentUser.nickname : `${currentUser.firstname} ${currentUser.lastname}` }}</v-toolbar-title>
              <v-spacer></v-spacer>
              <div v-if="currentlyHasAuthority">
                <v-btn @click="editProfile" outlined="true" class="mr-1">Edit</v-btn>
              </div>
            </v-toolbar>
            <v-card-text>
              <h3>Name</h3>
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

              <div v-if="currentUser.fitness">
                <p>Fitness level {{ currentUser.fitness }}</p>
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
                  outlined="true"
                >{{ country }}</v-chip>
                <v-chip
                  class="mr-1 mb-2"
                  v-if="currentUser.passports.length == 0"
                  outlined="true"
                >None</v-chip>
              </div>

            </v-card-text>
          </v-card>
        </v-col>
        <v-col sm="12" md="8" lg="8">
          <v-card class="elevation-12">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Activities</v-toolbar-title>
              <v-spacer></v-spacer>
              <div v-if="currentlyHasAuthority">
                <v-btn @click="createActivityClicked" outlined="true">Create Activity</v-btn>
              </div>
            </v-toolbar>

            <v-card-text>
              <div v-if="currentUser.activities && currentUser.activities.length > 0">
                <h3>Interests</h3>
                <v-chip
                  class="mr-2 mb-2"
                  v-for="activityType of currentUser.activities"
                  v-bind:key="activityType"
                >{{ activityType }}</v-chip>
              </div>
              <ActivitiesList :profileId="currentProfileId"></ActivitiesList>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
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
  getPermissionLevel
} from "../controllers/profile.controller";
import FormValidator from "../scripts/FormValidator";
// eslint-disable-next-line no-unused-vars
import { RegisterFormData } from "../controllers/register.controller";
import { getCurrentUser } from "../models/user.model";

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
      // newEmail: "",
      // email: "",
      editedUser: {} as UserApiFormat,
      formValidator: new FormValidator(),
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
    const permissionLevel: number = getPermissionLevel();
    if (isNaN(profileId)) {
      this.$router.push({ name: "login" });
    }
    this.currentProfileId = profileId;

    if (permissionLevel < 120) {
      getCurrentUser().then(user => {
        if (user.profile_id == profileId) {
          //if we're editing ourself
          this.currentlyHasAuthority = true;
        }
      });
    } else {
      this.currentlyHasAuthority = true;
    }

    fetchProfileWithId(profileId)
      .then(user => {
        this.currentUser = user;
      })
      .catch(err => {
        console.error(err);
      });
  },

  methods: {
    //click login button
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
      this.$router.push({ name: "createActivity" });
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
