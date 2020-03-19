<template>
  <div>
    <h1>Homepage</h1>
      <p>Homepage</p>

      <!--label for="currentUser.firstname">First Name:</label>
      <div class="field">
        <span class="field_value" v-show="!showField('currentUser.firstname')" @click="focusField('currentUser.firstname')">{{currentUser.firstname}}</span>
        <input v-model="currentUser.firstname" v-show="showField('currentUser.firstname')" id="currentUser.firstname" type="text" class="field-value form-control" @focus="focusField('currentUser.firstname')" >
      </div-->

      <!--div class="editable_text">
        {{message}}
      </div-->

      <!-- as per U3 AC3, user knows the limit of additional emails -->
      <p>Secondary Emails {{ currentUser.additional_email.length }} / 5:</p>
      <ul>
        <li v-for="email in currentUser.additional_email" :key="email">{{ email }}
          <v-btn @click="deleteEmailAddress(email)">delete</v-btn>
          <v-btn @click="setPrimaryEmail(email)">Make Primary</v-btn>
        </li>
      </ul>
      <div v-if="!editing">
        <p>First Name:{{ currentUser.firstname }}</p>
        <p>Middle name: {{currentUser.middlename}}</p>
        <p>Last Name: {{ currentUser.lastname }} </p>
        <p>Nickname: {{ currentUser.nickname }} </p>
        <p>Date of Birth: {{ currentUser.date_of_birth }} </p>
        <p>Bio: {{ currentUser.bio }} </p>
        <p>Gender: {{ currentUser.gender }} </p>
        <br>
        <button @click="editProfile">Edit Profile</button><br>

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

        <label for="fitnessDropdown">Select a Fitness Level:</label>
        <select id="fitnessDropdown" v-model="selectedFitnessLevel">
          <option value=-1>Not specified</option>
          <option value=0>Muffin</option>
          <option value=1>Potato</option>
          <option value=2>Carrot</option>
          <option value=3>Blueberry</option>
          <option value=4>Kale</option>
        </select>
        <button id="selectFitness" @click="selectFitnessLevel">Select</button>

        <p>Primary email: {{ currentUser.primaryEmail }}</p>
        <!-- New Email input field and button -->
        <input ref="newEmail" id="newEmail" type="email" v-model="newEmail" />
        <v-btn id="addEmailAddress" @click="addEmailAddress">Add Email</v-btn>
      </div>

      <br>
      <!-- <label>Enter Old Password <input ref="oldPassword" id="oldPassword" type="password" v-model="oldPassword" /></label>
      <label>Enter New Password <input ref="newPassword" id="newPassword" type="password" v-model="newPassword" /></label>
      <label>Repeat New Password <input ref="repeatPassword" id="repeatPassword" type="password" v-model="repeatPassword" /></label> -->
      <v-form>
      <v-text-field
        v-model="oldPassword"
        label="old password"
        type="password"
        required></v-text-field>
      <v-text-field
        v-model="newPassword"
        label="new password"
        type="password"
        required></v-text-field>
      <v-text-field
        v-model="repeatPassword"
        label="repeat password"
        type="password"
        required></v-text-field>
    </v-form>
      <v-alert type="error" v-if="passwordErrorMessage">{{ passwordErrorMessage }}</v-alert>
      <v-alert type="success" v-if="passwordSuccessMessage">{{ passwordSuccessMessage }}</v-alert>
      <v-btn id="updatePassword" @click="updatePasswordButtonClicked">Update your password</v-btn>

      <br>
      <br>
      <v-btn @click="logoutButtonClicked">Logout</v-btn>
      <div v-if="editing">
        <v-form>
          <v-text-field id="firstname" label="First name" v-model="currentUser.firstname" :rules="inputRules.firstnameRules"></v-text-field>
          <v-text-field id="middlename" label="Middle name" v-model="currentUser.middlename" :rules="inputRules.middlenameRules"></v-text-field>
          <v-text-field id="lastname" label="Last name" v-model="currentUser.lastname" :rules="inputRules.lastnameRules"></v-text-field>
          <v-text-field id="nickname" label="Nickname" v-model="currentUser.nickname" :rules="inputRules.nicknameRules"></v-text-field>
          <v-text-field id="bio" label="Bio" v-model="currentUser.bio" :rules="inputRules.bioRules"></v-text-field>
          <v-menu>
            <template v-slot:activator="{ on }">
            <v-text-field v-model="currentUser.dateOfBirth" :value="currentUser.dateOfBirth" v-on="on" label="Date of Birth" :rules="inputRules.dobRules"></v-text-field>
            </template>
            <v-date-picker v-model="currentUser.dateOfBirth"></v-date-picker>
          </v-menu>
          <v-select label="Gender" v-model="currentUser.gender" :items="genders" :rules="inputRules.genderRules"></v-select>
        </v-form>

        <button @click="saveButtonClicked">Save</button>
        <button @click="cancelButtonClicked">Cancel</button>
      </div>

  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import { UserApiFormat} from '../scripts/User'
  import { logoutCurrentUser, updatePassword, addPassportCountry, fetchCurrentUser, setFitnessLevel, editProfile, addEmail, deleteEmail, setPrimary } from '../controllers/profile.controller'
  import { checkFirstnameValidity, checkLastnameValidity, checkMiddlenameValidity, checkNicknameValidity, checkBioValidity, checkDobValidity, checkGenderValidity, isValidEmail } from '@/scripts/FormValidator'
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
        inputRules: {
          "lastnameRules": [(v: string) => checkLastnameValidity(v)],
          "firstnameRules": [(v: string) => checkFirstnameValidity(v)],
          "middlenameRules": [(v: string) => checkMiddlenameValidity(v)],
          "nicknameRules": [(v: string) => checkNicknameValidity(v)],
          "bioRules": [(v: string) => checkBioValidity(v)],
          "dobRules": [(v: string) => checkDobValidity(v)],
          "genderRules": [(v: string) => checkGenderValidity(v)]
        }
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


      // focusField(name){
      //     this.currentUser.firstname = name;
      // },

      // blurField(){
      //   this.currentUser.firstname = '';
      // },

      // showField(name){
      //   return (this.currentUser.firstname == '' || this.currentUser.firstname == name)
      // },

      //click login button
      logoutButtonClicked: function() {
        logoutCurrentUser()
          .then(() => {
            console.log("updatePassword")
          })
          .catch((err: any) => {
            console.error(err);
          })
      },

      //add passport country
      selectCountry: function () {

        if (this.currentUser && this.currentUser.primary_email) {
          addPassportCountry(this.selectedCountry, this.currentUser.primary_email)
            .then(() => {
              console.log('passport country added')
            })
            .catch((err: any) => {
              console.log(err)
            })
            // refresh page after adding passport
        history.go(0);
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
        if(isValidEmail(this.newEmail)) {
          addEmail(this.newEmail)
          .then(() => {
            console.log("Email address added");
          })
          .catch((err) => {
            console.log(err);
          })
          // refresh page after adding emails
          history.go(0);
        } else {
          alert("Not valid email")
        }
      },

      deleteEmailAddress: function (email: string) {
        deleteEmail(email)
        .then(() => {
          console.log("Email address deleted");
        })
        .catch((err: any) => {
          console.log(err);
        })
        // refresh page after deleting emails
        history.go(0);
      },

      setPrimaryEmail: function(email: string) {
        setPrimary(email)
        .then(() => {
          console.log("primary email changed");
        })
        .catch((err) => {
          console.log(err);
        })
        // refresh page after changing primary email
        history.go(0);
      },

      editProfile: function() {
        this.editing = true;
      },

      saveButtonClicked: function() {
        editProfile(this.currentUser);
        this.editing = false;
      },

      cancelButtonClicked: function() {
        alert("CANCELLED");
        this.editing = false;
      }
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
