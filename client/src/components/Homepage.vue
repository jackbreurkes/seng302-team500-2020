<template>
  <div>

      <v-container class="fill-height" fluid>
      <v-row
        align="center"
        justify="center"
      >
        <v-col
          cols="12"
          sm="8"
          md="4"
        >
          <v-card class="elevation-12" width=100%>
            <v-toolbar
              color="primary"
              dark
              flat
            >
            <v-toolbar-title>Profile: {{ currentUser.firstname }} {{ currentUser.lastname }} </v-toolbar-title>
            </v-toolbar>

      <!-- as per U3 AC3, user knows the limit of additional emails -->





       <v-card-text>
      <div v-if="!editing">
        <p>First Name: {{ currentUser.firstname }}</p><br>
        <p>Middle name: {{currentUser.middlename}}</p><br>
        <p>Last Name: {{ currentUser.lastname }} </p><br>
        <p>Nickname: {{ currentUser.nickname }} </p><br>
        <p>Date of Birth: {{ currentUser.date_of_birth }} </p><br>
        <p>Bio: {{ currentUser.bio }} </p><br>
        <p>Gender: {{ currentUser.gender }} </p><br>
        <br>
  

        <label for="countryDropdown">Select a Country:</label>
        <select id="countryDropdown" v-model="selectedCountry">
          <option value="" selected disabled>choose a country</option>
          <option value="" v-if="passportCountries.length === 0" disabled>please wait...</option>
          <option v-for="country in passportCountries" :key="country.numericCode" :value="country">{{ country.name }}</option>
        </select>
        <button id="selectCountry" @click="selectCountry">Select</button>
        <ul id="passports">
          <li v-for="passport in currentUser.passports" :key="passport">{{ passport }}</li>
        </ul>
        <br>
        <label for="fitnessDropdown">Select a Fitness Level:</label>
        <select id="fitnessDropdown" v-model="selectedFitnessLevel">
          <option value=-1>Not specified</option>
          <option value=0>Muffin -No fitness</option>
          <option value=1>Potato -Little fitness</option>
          <option value=2>Carrot -Moderate fitness</option>
          <option value=3>Blueberry -Outdoors enthusiast</option>
          <option value=4>Kale -Professional athlete</option>
        </select>
        <v-btn id="selectFitness" @click="selectFitnessLevel">Select Level of fitness</v-btn>
        <br>

        <p>Primary email: {{ currentUser.primary_email }}</p>
        <!-- New Email input field and button -->
        <br>
        <p>Secondary Emails {{ currentUser.additional_email.length }} / 5:</p>
        <ul>
          <li v-for="email in currentUser.additional_email" :key="email">{{ email }}
          <v-btn @click="deleteEmailAddress(email)">delete</v-btn>
          <v-btn @click="setPrimaryEmail(email)">Make Primary</v-btn>
          </li>
        </ul>
        <br>
        <p>Add a new email: </p>
        <!-- <input ref="newEmail" id="newEmail" type="email" v-model="newEmail" /> -->
        <v-card-actions>
        <v-text-field
        v-model="newEmail"
        label="enter new email here"
        type="email"
        dense
        filled
        required></v-text-field>
        
        <v-btn id="addEmailAddress"  @click="addEmailAddress" >Add Email</v-btn>
        </v-card-actions>
        <br>
        <br>
        <v-btn @click="editProfile">Edit Profile</v-btn>
        <v-btn @click="logoutButtonClicked">Logout</v-btn>
      </div>

      <div v-if="editing">
        <v-form ref="editForm">
          <v-text-field dense filled id="firstname" label="First name" v-model="editedUser.firstname" :rules="inputRules.firstnameRules"></v-text-field>
          <v-text-field dense filled id="middlename" label="Middle name" v-model="editedUser.middlename" :rules="inputRules.middlenameRules"></v-text-field>
          <v-text-field dense filled id="lastname" label="Last name" v-model="editedUser.lastname" :rules="inputRules.lastnameRules"></v-text-field>
          <v-text-field dense filled id="nickname" label="Nickname" v-model="editedUser.nickname" :rules="inputRules.nicknameRules"></v-text-field>
          <v-text-field dense filled id="bio" label="Bio" v-model="editedUser.bio" :rules="inputRules.bioRules"></v-text-field>
          <v-menu>
            <template v-slot:activator="{ on }">
            <v-text-field dense filled  v-model="editedUser.dateOfBirth" :value="editedUser.dateOfBirth" v-on="on" label="Date of Birth" :rules="inputRules.dobRules"></v-text-field>
            </template>
            <v-date-picker v-model="editedUser.dateOfBirth"></v-date-picker>
          </v-menu>
          <v-select dense filled  label="Gender" v-model="editedUser.gender" :items="genders" :rules="inputRules.genderRules"></v-select>
        </v-form>

        <v-btn @click="saveButtonClicked">Save</v-btn>
        <v-btn @click="cancelButtonClicked">Cancel</v-btn>
      </div>

      <br>
      <v-form>
      <v-text-field
        v-model="oldPassword"
        label="old password"
        type="password"
        dense
        filled
        required></v-text-field>
      <v-text-field
        v-model="newPassword"
        label="new password"
        type="password"
                dense
        filled
        required></v-text-field>
      <v-text-field
        v-model="repeatPassword"
        label="repeat password"
        type="password"
        dense
        filled
        required></v-text-field>
    </v-form>
      <v-alert type="error" v-if="passwordErrorMessage">{{ passwordErrorMessage }}</v-alert>
      <v-alert type="success" v-if="passwordSuccessMessage">{{ passwordSuccessMessage }}</v-alert>
      <v-btn id="updatePassword" @click="updatePasswordButtonClicked">Update your password</v-btn>

      <br>
      <br>

       </v-card-text>
        </v-card>
      </v-col>
    </v-row>
</v-container>
  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import { UserApiFormat} from '../scripts/User';
  import { logoutCurrentUser, updatePassword, addPassportCountry, fetchCurrentUser, setFitnessLevel, editProfile, addNewEmail, deleteEmail, setPrimary } from '../controllers/profile.controller';
  //import { checkFirstnameValidity, checkLastnameValidity, checkMiddlenameValidity, checkNicknameValidity, checkBioValidity, checkDobValidity, checkGenderValidity, isValidEmail } from '../scripts/FormValidator'
  import * as FormValidator from '../scripts/FormValidator';
  // eslint-disable-next-line no-unused-vars
  import { RegisterFormData } from '../controllers/register.controller';


  // app Vue instance
const Homepage =  Vue.extend({
    name: 'Homepage',

    // app initial state
    data: function() {
      return {
        currentUser: {} as UserApiFormat,
        passportCountries: [],
        selectedCountry: "" as any,
        selectedFitnessLevel: -1,
        newEmail: "",
        email: "",
        oldPassword: '',
        newPassword: '',
        repeatPassword: '',
        passwordErrorMessage: '',
        passwordSuccessMessage: '',
        genders: ["Male", "Female", "Non-binary"],
        editing: false,
        editedUser: {} as UserApiFormat,
        inputRules: {
          "lastnameRules": [(v: string) => FormValidator.checkLastnameValidity(v) || FormValidator.LAST_NAME_ERROR_STRING],
          "firstnameRules": [(v: string) => FormValidator.checkFirstnameValidity(v) || FormValidator.FIRST_NAME_ERROR_STRING],
          "middlenameRules": [(v: string) => FormValidator.checkMiddlenameValidity(v) || FormValidator.MIDDLE_NAME_ERROR_STRING],
          "nicknameRules": [(v: string) => FormValidator.checkNicknameValidity(v) || FormValidator.NICKNAME_ERROR_STRING],
          "bioRules": [(v: string) => FormValidator.checkBioValidity(v) || FormValidator.BIO_ERROR_STRING],
          "dobRules": [(v: string) => FormValidator.checkDobValidity(v) || FormValidator.DOB_ERROR_STRING],
          "genderRules": [(v: string) => FormValidator.checkGenderValidity(v) || FormValidator.GENDER_ERROR_STRING]
        },
      }
    },

    created() {
      fetchCurrentUser()
        .then((user) => {
          this.currentUser = user;
          if (this.currentUser) {
            this.selectedFitnessLevel = this.currentUser.fitness || -1;
          }
        })
        .catch((err) => {
          console.error(err);
        });

      const Http = new XMLHttpRequest();
      const url='https://restcountries.eu/rest/v2/all';
      Http.open("GET", url, true);
      Http.send();

      Http.onreadystatechange = () => {
        try {
          const countries = JSON.parse(Http.responseText)
          this.passportCountries = countries
        } catch (err) {
          console.error(err)
        }
      }
    },

    methods: {
      updatePasswordButtonClicked: function(){
        this.passwordSuccessMessage = "";
        this.passwordErrorMessage = "";
        updatePassword(this.oldPassword,this.newPassword,this.repeatPassword)
          .then(() => {
            this.passwordSuccessMessage = "password changed successfully";
          })
          .catch((err: any) => {
            this.passwordErrorMessage = err.message;
          })

      },

      //click login button
      logoutButtonClicked: function() {
        logoutCurrentUser()
          .then(() => {
            this.$router.push({name: "login"});
          })
          .catch((err: any) => {
            console.error(err);
            this.$router.push({name: "login"});
          })
      },

      //add passport country
      selectCountry: function () {

        if (this.currentUser && this.currentUser.primary_email) {
          addPassportCountry(this.selectedCountry, this.currentUser.primary_email)
            .then(() => {
              console.log('passport country added')
            // refresh page after adding passport
            history.go(0);
            })
            .catch((err: any) => {
              console.log(err)
            })
          }
      },

      selectFitnessLevel: function () {
        if (this.currentUser && this.currentUser.profile_id) {
          setFitnessLevel(this.selectedFitnessLevel, this.currentUser.profile_id)
          .then(() => {
            console.log("Fitness level set");
          })
          .catch((err: any) => {
            console.log(err);
          })
        }
      },

      addEmailAddress: function() {
        addNewEmail(this.newEmail)
        .then(() => {
          console.log("Email address added");
          // refresh page after adding emails
          history.go(0);
        })
        .catch((err) => {
          console.log(err);
        })
      },

      deleteEmailAddress: function (email: string) {
        deleteEmail(email)
        .then(() => {
          // refresh page after deleting emails
          history.go(0);
        })
        .catch((err: any) => {
          console.log(err);
        })
      },

      setPrimaryEmail: function(email: string) {
        setPrimary(email) // Does not validate the email as it is a requirement that the email must already be registered to the user (hence, has previously been validated);
        .then(() => {
          // refresh page after changing primary email
          history.go(0);
        })
        .catch((err) => {
          console.log(err);
        })
      },

      editProfile: function() {
        Object.assign(this.editedUser, this.currentUser);
        this.editing = true;
      },

      saveButtonClicked: async function() {
        if ((this.$refs.editForm as Vue & { validate: () => boolean }).validate()) {
          try {
            await editProfile(this.editedUser);
            Object.assign(this.currentUser, this.editedUser);
            this.editing = false;
          } catch {
            alert("Unable to update user.");
          }
        }        
      },

      cancelButtonClicked: function() {
        this.editing = false;
      }
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
