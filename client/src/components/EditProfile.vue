<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6">
          <v-card class="elevation-12" width="100%">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>Profile: {{ titleBarUserName }}</v-toolbar-title>
            </v-toolbar>

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
                <v-textarea
                  dense
                  filled
                  id="bio"
                  label="Bio"
                  v-model="editedUser.bio"
                  :rules="inputRules.bioRules"
                >Something in here</v-textarea>
                <template>
                  <v-container-fluid>
                    <v-radio-group v-model="userFitnessLevel" label="Fitness Level" :mandatory="false">
                      <v-radio label="Muffin (no fitness)" value="0"></v-radio>
                      <v-radio label="Potato (little fitness)" value="1"></v-radio>
                      <v-radio label="Carrot (moderate fitness)" value="2"></v-radio>
                      <v-radio label="Blueberry (outdoors enthusiast)" value="3"></v-radio>
                      <v-radio label="Kale (fitness fanatic)" value="4"></v-radio>
                    </v-radio-group>
                  </v-container-fluid>
                </template>

                <v-divider></v-divider>

                <v-card-title>Personal Details</v-card-title>

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

                <v-select
                  dense
                  filled
                  label="Gender"
                  v-model="editedUser.gender"
                  :items="availableGenders"
                  :rules="inputRules.genderRules"
                ></v-select>

                <v-menu
                  ref="dobMenu"
                  v-model="dobMenu"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template v-slot:activator="{ on }">
                    <v-text-field
                      dense
                      filled
                      v-model="editedUser.date_of_birth"
                      :value="editedUser.date_of_birth"
                      v-on="on"
                      readonly
                      label="Date of Birth"
                      :rules="inputRules.dobRules"
                    ></v-text-field>
                  </template>
                  <v-date-picker no-title v-model="editedUser.date_of_birth" @input="dobMenu = false"></v-date-picker>
                </v-menu>

                <div id="passport-chips" v-if="editedUser.passports && editedUser.passports.length > 0">
                  <v-chip
                    v-for="passport in editedUser.passports"
                    :key="passport"
                    close
                    class="ma-1"
                    @click:close="deletePassportCountry(passport)"
                  >{{ passport }}</v-chip>
                </div>

                <v-autocomplete
                  :items="passportCountries"
                  color="white"
                  item-text="name"
                  label="Countries"
                  v-model="selectedCountry"
                  @input="addSelectedPassportCountry()"
                ></v-autocomplete>

              </v-form>

              <v-btn @click="saveButtonClicked">Save</v-btn>
              <v-btn @click="returnToProfile">Cancel</v-btn>

              <br />
              <br />
            </v-card-text>
          </v-card>
          <br />

          <v-card>
            <v-card-title>Login Details</v-card-title>
            <v-card-text>
              <p>Primary email: {{ editedUser.primary_email }}</p>
                <br />
                <p>Secondary Emails {{ (editedUser.additional_email !== undefined && editedUser.additional_email.length) || 0 }} / 5:</p>
                <ul>
                  <li v-for="email in editedUser.additional_email" :key="email">
                    {{ email }}
                    <v-btn @click="deleteEmailAddress(email)">delete</v-btn>
                    <v-btn @click="setPrimaryEmail(email)">Make Primary</v-btn>
                  </li>
                </ul>
                <br />
                <p>Add a new email:</p>
                <v-card-actions v-if="editedUser.additional_email.length !== 5">
                  <v-text-field
                    v-model="newEmail"
                    label="enter new email here"
                    type="email"
                    dense
                    filled
                    required
                    :rules="inputRules.emailRules"
                  ></v-text-field>

                  <v-btn id="addEmailAddress"  @click="addTempEmail(newEmail)">Add Email</v-btn>
                  
                </v-card-actions>
                <v-btn id="updateEmail"  @click="updateEmail()">Update Email</v-btn>
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
                  <v-alert type="error" v-if="passwordErrorMessage">{{ passwordErrorMessage }}</v-alert>
                  <v-alert type="success" v-if="passwordSuccessMessage">{{ passwordSuccessMessage }}</v-alert>
                  <v-btn id="updatePassword" @click="updatePasswordButtonClicked">Update your password</v-btn>
                </v-form>
              <!-- insert edit email and edit password forms here -->
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
  fetchProfileWithId,
  persistChangesToProfile,
  updatePassword,
  setPrimary,
  updateNewEmailList,
  // isValidEmail
  getAvailablePassportCountries,
  addPassportCountry,
  deletePassportCountry
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
      titleBarUserName: "",
      currentProfileId: NaN as number,
      editedUser: {} as UserApiFormat,
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
        ],
        emailRules: [
          (v: string) =>
          formValidator.isValidEmail(v) || formValidator.EMAIL_ERROR_STRING

        ]
      },

      // values pertaining Personal Details section
      passportCountries: [] as string[],
      selectedCountry: "" as string,
      availableGenders: ["male", "female", "non-binary"], // casing is dependent on API spec
      dobMenu: false,
      userFitnessLevel: "",
      newEmail: "" as string,
      oldPassword: "",
      newPassword: "",
      repeatPassword: "",
      passwordErrorMessage: "",
      passwordSuccessMessage: "",
      
    };
  },

  /**
   * runs when the page is created
   */
  created() {
    // load profile info
    const profileId: number = parseInt(this.$route.params.profileId);
    if (isNaN(profileId)) {  // profile id in route not a number
      this.$router.push({ name: "login" });
    }
    this.currentProfileId = profileId;
    fetchProfileWithId(profileId)
      .then(user => {
        this.titleBarUserName = `${user.firstname} ${user.lastname}`;
        this.editedUser = user;
        if (this.editedUser.fitness) {
          this.userFitnessLevel = this.editedUser.fitness.toString();
        }
      })
      .catch(err => {
        console.error(err);
      });

    getAvailablePassportCountries()
      .then(countries => {
        this.passportCountries = countries})
      .catch(err => {console.error("unable to load passport countries");
      console.error(err)});
  },

  methods: {
    /**
     * adds the passport country selected in the dropdown to the current user's passport countries
     */
    addSelectedPassportCountry: async function() {
      if (!this.selectedCountry) {
        return
      }
      await addPassportCountry(this.selectedCountry, this.editedUser);
    },

    /**
     * removes the given country from the passport countries of the user being edited
     */
    deletePassportCountry: function(country: string) {
      deletePassportCountry(country, this.editedUser);
    },

    /**
     * persists the changes made on the edit page by the user
     */
    saveButtonClicked: async function() {
      if (this.userFitnessLevel !== "") { // converts the fitness level v-model string into an integer
        this.editedUser.fitness = parseInt(this.userFitnessLevel);
      }
      if (
        (this.$refs.editForm as Vue & { validate: () => boolean }).validate()
      ) {
        persistChangesToProfile(this.editedUser, this.currentProfileId)
        .then(() => {this.returnToProfile()})
        .catch(() => {alert("Unable to update user.");});
      }
    },

    /**
     * returns the user to the profile page of the profile they are editing
     */
    returnToProfile: function() {
      this.$router.push("/profiles/" + this.currentProfileId)
    },

    /**
     * Copies the current editedusers secondary emails to current user
     */
    updateEmail: function() {
      if (this.editedUser.primary_email === undefined) {
        this.editedUser.primary_email = ""
      }
      if (this.editedUser.additional_email === undefined) {
        this.editedUser.additional_email = []
      }
      setPrimary(this.editedUser.primary_email, this.currentProfileId)
      updateNewEmailList(this.editedUser.additional_email, this.currentProfileId)
        .then(() => {
          console.log("Email address added");
           // refresh page after adding emails
          history.go(0);
        })
        .catch(err => {
          console.log(err);
        })
    },

    deleteEmailAddress: function(email: string) {
      if (this.editedUser.additional_email === undefined) {
        this.editedUser.additional_email = []
      }
      let index = this.editedUser.additional_email.indexOf(email);
      this.editedUser.additional_email.splice(index, 1);
    },

    
    setPrimaryEmail: function(email: string) {
      let oldPrimaryEmail = this.editedUser.primary_email;
      this.editedUser.primary_email = email;
      if (this.editedUser.additional_email === undefined) {
        this.editedUser.additional_email = []
      }
      if (oldPrimaryEmail !== undefined) {
        this.editedUser.additional_email.splice(this.editedUser.additional_email.indexOf(email), 1, oldPrimaryEmail);
      } else {
       this.editedUser.additional_email.splice(this.editedUser.additional_email.indexOf(email), 1);
      }
    },

    addTempEmail: function(email: string) {

      if(new FormValidator().isValidEmail(email)){
        if (this.editedUser.additional_email === undefined) {
              this.editedUser.additional_email = []
        }
        else {
          this.editedUser.additional_email.push(email);
        }
      }
    },

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

      this.oldPassword = "";
      this.newPassword = "";
      this.repeatPassword = ""; 
    },



    
  }
});

export default Homepage;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
