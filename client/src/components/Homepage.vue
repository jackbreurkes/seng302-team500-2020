<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="top" justify="center">
        <v-col sm="12" md="8" lg="4">
          <v-card class="elevation-12">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Profile: {{ currentUser.firstname }} {{ currentUser.lastname }}</v-toolbar-title>
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

                <p>Primary email: {{ currentUser.primary_email }}</p>
                <br />
                <div v-if="currentUser.additional_email && currentUser.additional_email.length > 0">
                  <p>Secondary Emails {{ currentUser.additional_email ? currentUser.additional_email.length : 0 }} / 5:</p>
                  <ul>
                    <li v-for="email in currentUser.additional_email" :key="email">{{ email }}</li>
                  </ul>
                </div>
                
                <div v-if="currentUser.passports && currentUser.passports.length > 0">
                  <h3>Passport Countries</h3>
                  <v-chip
                    class="mr-2 mb-2"
                    v-for="country of currentUser.passports"
                    v-bind:key="country"
                  >{{ country }}</v-chip>
                </div>

                <br />
                <v-btn @click="editProfile">Edit Profile</v-btn>
                <v-btn @click="createActivityClicked">Create Activity</v-btn>

            </v-card-text>
          </v-card>
        </v-col>
        <v-col sm="12" md="8" lg="8">
          <v-card class="elevation-12">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Activities</v-toolbar-title>
            </v-toolbar>

            <v-card-text>
              <div v-if="currentUser.activities">
                <h3 v-if="currentUser.activities && currentUser.activities.length > 0">Interests</h3>
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
  fetchProfileWithId
} from "../controllers/profile.controller";
import FormValidator from "../scripts/FormValidator";
// eslint-disable-next-line no-unused-vars
import { RegisterFormData } from "../controllers/register.controller";

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
    if (isNaN(profileId)) {
      this.$router.push({ name: "login" });
    }
    this.currentProfileId = profileId;

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
