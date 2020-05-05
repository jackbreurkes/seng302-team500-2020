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

                <h3>Bio</h3>
                <p>{{ currentUser.bio }}</p>
                <br />

                <h3>Info</h3>
                <p>Born on {{ currentUser.date_of_birth }}</p>
                <br />
                <p>Identifies as {{ currentUser.gender }}</p>
                <br />

                <div v-if="currentUser.fitness">
                  <p>Fitness level {{ currentUser.fitness }}</p>
                  <br />
                </div>
                <p>Email: {{ currentUser.primary_email }}</p>
                <br />

                <div v-if="currentUser.passports">
                  <h3>Passport Countries</h3>
                  <v-chip
                    class="mr-2 mb-2"
                    v-for="country of currentUser.passports"
                    v-bind:key="country"
                  >{{ country }}</v-chip>
                </div>

                <p>Primary email: {{ currentUser.primary_email }}</p>
                <br />
                <p>Secondary Emails {{ (currentUser.additional_email !== undefined && currentUser.additional_email.length) || 0 }} / 5:</p>
                <ul>
                  <li v-for="email in currentUser.additional_email" :key="email">{{ email }}</li>
                </ul>
                <br />
                <v-btn v-if="currentProfileId == currentSessionUser" @click="editProfile">Edit Profile</v-btn>
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
                <h3>Interests</h3>
                <v-chip
                  class="mr-2 mb-2"
                  v-for="activityType of currentUser.activities"
                  v-bind:key="activityType"
                >{{ activityType }}</v-chip>
              </div>insert activities here
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "../scripts/User";
import {
    logoutCurrentUser,
    fetchProfileWithId, fetchCurrentUser
    // addNewEmail,
    // deleteEmail,
    // setPrimary
} from "../controllers/profile.controller";
import FormValidator from "../scripts/FormValidator";
// eslint-disable-next-line no-unused-vars
import { RegisterFormData } from "../controllers/register.controller";

// app Vue instance
const Homepage = Vue.extend({
  name: "Homepage",

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
    return {
      currentProfileId: NaN as number,
      currentUser: {} as UserApiFormat,
      currentSessionUser: NaN as number,
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
    fetchCurrentUser().then((user) => {
        this.currentSessionUser = user.profile_id!;
        }
    );
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

    // addEmailAddress: function() {
    //   addNewEmail(this.newEmail, this.currentProfileId)
    //     .then(() => {
    //       console.log("Email address added");
    //       // refresh page after adding emails
    //       history.go(0);
    //     })
    //     .catch(err => {
    //       console.log(err);
    //     });
    // },

    // deleteEmailAddress: function(email: string) {
    //   deleteEmail(email, this.currentProfileId)
    //     .then(() => {
    //       // refresh page after deleting emails
    //       history.go(0);
    //     })
    //     .catch((err: any) => {
    //       console.log(err);
    //       // refresh page hopefully fixing issues
    //       history.go(0);
    //     });
    // },

    // setPrimaryEmail: function(email: string) {
    //   setPrimary(email, this.currentProfileId) // Does not validate the email as it is a requirement that the email must already be registered to the user (hence, has previously been validated);
    //     .then(() => {
    //       // refresh page after changing primary email
    //       history.go(0);
    //     })
    //     .catch(err => {
    //       console.log(err);
    //       // refresh page to hopefully fix issues
    //       history.go(0);
    //     });
    // },

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
