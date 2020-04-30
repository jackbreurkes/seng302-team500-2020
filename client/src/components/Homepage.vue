<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="4">
          <v-card class="elevation-12" width="100%">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Profile: {{ currentUser.firstname }} {{ currentUser.lastname }}</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
              <div v-if="!editing">
                <p>First Name: {{ currentUser.firstname }}</p>
                <br />
                <p>Middle name: {{currentUser.middlename}}</p>
                <br />
                <p>Last Name: {{ currentUser.lastname }}</p>
                <br />
                <p>Nickname: {{ currentUser.nickname }}</p>
                <br />
                <p>Date of Birth: {{ currentUser.date_of_birth }}</p>
                <br />
                <p>Bio: {{ currentUser.bio }}</p>
                <br />
                <p>Gender: {{ currentUser.gender }}</p>
                <br />
                <br />

                <p>Primary email: {{ currentUser.primary_email }}</p>
                <br />
                <p>Secondary Emails {{ (currentUser.additional_email !== undefined && currentUser.additional_email.length) || 0 }} / 5:</p>
                <ul>
                  <li v-for="email in currentUser.additional_email" :key="email">
                    {{ email }}
                    <v-btn @click="deleteEmailAddress(email)">delete</v-btn>
                    <v-btn @click="setPrimaryEmail(email)">Make Primary</v-btn>
                  </li>
                </ul>
                <br />
                <p>Add a new email:</p>
                <v-card-actions>
                  <v-text-field
                    v-model="newEmail"
                    label="enter new email here"
                    type="email"
                    dense
                    filled
                    required
                  ></v-text-field>

                  <v-btn id="addEmailAddress" @click="addEmailAddress">Add Email</v-btn>
                </v-card-actions>
                <br />
                <v-btn @click="editProfile">Edit Profile</v-btn>
                <v-btn @click="logoutButtonClicked">Logout</v-btn>
                <v-btn @click="createActivityClicked">Create Activity</v-btn>
              </div>
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
  fetchProfileWithId,
  addNewEmail,
  deleteEmail,
  setPrimary
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
      newEmail: "",
      email: "",
      editing: false,
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

    addEmailAddress: function() {
      addNewEmail(this.newEmail, this.currentProfileId)
        .then(() => {
          console.log("Email address added");
          // refresh page after adding emails
          history.go(0);
        })
        .catch(err => {
          console.log(err);
        });
    },

    deleteEmailAddress: function(email: string) {
      deleteEmail(email, this.currentProfileId)
        .then(() => {
          // refresh page after deleting emails
          history.go(0);
        })
        .catch((err: any) => {
          console.log(err);
          // refresh page hopefully fixing issues
          history.go(0);
        });
    },

    setPrimaryEmail: function(email: string) {
      setPrimary(email, this.currentProfileId) // Does not validate the email as it is a requirement that the email must already be registered to the user (hence, has previously been validated);
        .then(() => {
          // refresh page after changing primary email
          history.go(0);
        })
        .catch(err => {
          console.log(err);
          // refresh page to hopefully fix issues
          history.go(0);
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
