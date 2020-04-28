<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="4">
          <v-card class="elevation-12" width="100%">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Profile: {{ currentUser.firstname }} {{ currentUser.lastname }}</v-toolbar-title>
            </v-toolbar>

            <!-- as per U3 AC3, user knows the limit of additional emails -->

            <v-card-text>
              <v-card-title>About Me</v-card-title>
              <v-form ref="editForm">
                <v-text-field
                  dense
                  filled
                  id="nickname"
                  label="Nickname"
                  v-model="editedUser.nickname"
                  :rules="inputRules.nicknameRules"
                ></v-text-field>
                <v-text-field
                  dense
                  filled
                  id="bio"
                  label="Bio"
                  v-model="editedUser.bio"
                  :rules="inputRules.bioRules"
                ></v-text-field>
      
          <template>
            <v-container-fluid>
              <p>Fitness Level</p>
              <v-radio-group :mandatory="false">
                <v-radio label="Muffin (no fitness)" value="muffin"></v-radio>
                <v-radio label="Potato (little fitness)" value="potato"></v-radio>
                <v-radio label="Carrot (moderate fitness)" value="carrot"></v-radio>
                <v-radio label="Blueberry (outdoors enthusiast)" value="blueberry"></v-radio>
                <v-radio label="Kale (fitness fanatic)" value="kale"></v-radio>
              </v-radio-group>
            </v-container-fluid>
          </template>

                <v-text-field
                  dense
                  filled
                  id="firstname"
                  label="First name"
                  v-model="editedUser.firstname"
                  :rules="inputRules.firstnameRules"
                ></v-text-field>
                <v-text-field
                  dense
                  filled
                  id="middlename"
                  label="Middle name"
                  v-model="editedUser.middlename"
                  :rules="inputRules.middlenameRules"
                ></v-text-field>
                <v-text-field
                  dense
                  filled
                  id="lastname"
                  label="Last name"
                  v-model="editedUser.lastname"
                  :rules="inputRules.lastnameRules"
                ></v-text-field>

                <v-menu>
                  <template v-slot:activator="{ on }">
                    <v-text-field
                      dense
                      filled
                      v-model="editedUser.dateOfBirth"
                      :value="editedUser.dateOfBirth"
                      v-on="on"
                      label="Date of Birth"
                      :rules="inputRules.dobRules"
                    ></v-text-field>
                  </template>
                  <v-date-picker v-model="editedUser.dateOfBirth"></v-date-picker>
                </v-menu>
                <v-select
                  dense
                  filled
                  label="Gender"
                  v-model="editedUser.gender"
                  :items="genders"
                  :rules="inputRules.genderRules"
                ></v-select>
              </v-form>

              <v-btn @click="saveButtonClicked">Save</v-btn>
              <v-btn @click="cancelButtonClicked">Cancel</v-btn>

              <br />
              <br />
            </v-card-text>
          </v-card>
          <br />

          <v-card>
            <v-card-title>Login Details</v-card-title>
            <v-card-text>
              <br />
              <v-form>
                <v-text-field
                  v-model="oldPassword"
                  label="old password"
                  type="password"
                  dense
                  filled
                  required
                ></v-text-field>
                <v-text-field
                  v-model="newPassword"
                  label="new password"
                  type="password"
                  dense
                  filled
                  required
                ></v-text-field>
                <v-text-field
                  v-model="repeatPassword"
                  label="repeat password"
                  type="password"
                  dense
                  filled
                  required
                ></v-text-field>
              </v-form>
              <v-alert type="error" v-if="passwordErrorMessage">{{ passwordErrorMessage }}</v-alert>
              <v-alert type="success" v-if="passwordSuccessMessage">{{ passwordSuccessMessage }}</v-alert>
              <v-btn id="updatePassword" @click="updatePasswordButtonClicked">Update your password</v-btn>
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
  updatePassword,
  addPassportCountry,
  deletePassportCountry,
  fetchProfileWithId,
  setFitnessLevel,
  editProfile,
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
      passportCountries: [],
      selectedCountry: "" as any,
      selectedPassport: 0,
      selectedFitnessLevel: -1,
      newEmail: "",
      email: "",
      oldPassword: "",
      newPassword: "",
      repeatPassword: "",
      passwordErrorMessage: "",
      passwordSuccessMessage: "",
      genders: ["Male", "Female", "Non-binary"],
      passportsNotEmpty: true,
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
        if (this.currentUser) {
          this.selectedFitnessLevel = this.currentUser.fitness || -1;
          if (
            this.currentUser.passports &&
            this.currentUser.passports.length != 0
          ) {
            this.passportsNotEmpty = true;
          } else {
            this.passportsNotEmpty = false;
          }
        }
      })
      .catch(err => {
        console.error(err);
      });

    const Http = new XMLHttpRequest();
    const url = "https://restcountries.eu/rest/v2/all";
    Http.open("GET", url, true);
    Http.send();

    Http.onreadystatechange = () => {
      try {
        const countries = JSON.parse(Http.responseText);
        this.passportCountries = countries;
      } catch (err) {
        console.error(err);
      }
    };
  },

  methods: {
    updatePasswordButtonClicked: function() {
      this.passwordSuccessMessage = "";
      this.passwordErrorMessage = "";
      updatePassword(
        this.oldPassword,
        this.newPassword,
        this.repeatPassword,
        this.currentProfileId
      )
        .then(() => {
          this.passwordSuccessMessage = "password changed successfully";
        })
        .catch((err: any) => {
          this.passwordErrorMessage = err.message;
        });
    },

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

    //add passport country
    selectCountry: function() {
      if (this.currentUser && this.currentUser.primary_email) {
        addPassportCountry(this.selectedCountry, this.currentProfileId)
          .then(() => {
            console.log("passport country added");
            this.passportsNotEmpty = true;
            history.go(0);
          })
          .catch((err: any) => {
            console.log(err);
          });
      }
    },

    deletePassportCountry: function() {
      if (
        this.currentUser &&
        this.currentUser.passports &&
        this.selectedPassport !== undefined
      ) {
        deletePassportCountry(
          this.currentUser.passports[this.selectedPassport],
          this.currentProfileId
        )
          .then(() => {
            if (
              this.currentUser.passports &&
              this.currentUser.passports.length == 0
            ) {
              this.passportsNotEmpty = false;
            }
            // refresh page after removing passport
            history.go(0);
          })
          .catch((err: any) => {
            console.log(err);
            // refresh page in hopes it will resolve error (e.g. user has removed passport in duplicate tab)
            history.go(0);
          });
      }
    },

    selectFitnessLevel: function() {
      if (this.currentUser && this.currentProfileId) {
        setFitnessLevel(this.selectedFitnessLevel, this.currentProfileId)
          .then(() => {
            console.log("Fitness level set");
          })
          .catch((err: any) => {
            console.log(err);
          });
      }
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
      Object.assign(this.editedUser, this.currentUser);
    },

    saveButtonClicked: async function() {
      if (
        (this.$refs.editForm as Vue & { validate: () => boolean }).validate()
      ) {
        try {
          await editProfile(this.editedUser, this.currentProfileId);
          Object.assign(this.currentUser, this.editedUser);
        } catch {
          alert("Unable to update user.");
        }
      }
    },

    cancelButtonClicked: function() {}
  }
});

export default Homepage;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
