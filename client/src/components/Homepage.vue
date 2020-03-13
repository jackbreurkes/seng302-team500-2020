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

      <p>First Name:{{ currentUser.firstname }}</p>
      <p>Middle name: {{currentUser.middlename}}</p>
      <p>Last Name: {{ currentUser.lastname }} </p>
      <p>Nickname: {{ currentUser.nickname }} </p>
      <p>Date of Birth: {{ currentUser.date_of_birth }} </p>
      <p>Bio: {{ currentUser.bio }} </p>
      <p>Gender: {{ currentUser.gender }} </p>





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
      <button id="addEmail" @click="addEmailAddress">Add Email</button>

      <ul>
        <li v-for="email in currentUser.secondaryEmails" :key="email">{{ email }}</li>
      </ul>

      <br>
      <button>Edit Profile</button>

      <br>
      <button @click="logoutButtonClicked">Logout</button>

      <v-form>
        <v-text-field id="firstname" label="First name" v-model="currentUser.firstname"></v-text-field>
        <v-text-field id="middlename" label="Middle name" v-model="currentUser.middlename"></v-text-field>
        <v-text-field id="lastname" label="Last name" v-model="currentUser.lastname"></v-text-field>
        <v-text-field id="nickname" label="Nickname" v-model="currentUser.nickname"></v-text-field>
        <v-text-field id="bio" label="Bio" v-model="currentUser.bio"></v-text-field>
        <v-menu>
          <template v-slot:activator="{ on }">
          <v-text-field :value="currentUser.dateOfBirth" v-on="on" label="Date of Birth"></v-text-field>
          </template>
          <v-date-picker v-model="currentUser.dateOfBirth"></v-date-picker>
        </v-menu>
        <v-select label="Gender" v-model="currentUser.gender" :items="genders"></v-select>
      </v-form>

    <button @click="saveButtonClicked">Save</button>
    <button @click="cancelButtonClicked">Cancel</button>

  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import { UserApiFormat} from '../scripts/User'
  import { logoutCurrentUser, addPassportCountry, fetchCurrentUser, setFitnessLevel, editProfile } from '../controllers/profile.controller'
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
        selectedFitnessLevel: 0,
        newEmail: "",
        genders: ["Male", "Female", "Non-binary"],
      }
    },

    created() {
      fetchCurrentUser()
        .then((user) => {
          this.currentUser = user;
          this.selectedFitnessLevel = this.currentUser.fitness;
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
            this.$router.push({ name: "initialPage" })
          })
          .catch((err) => {
            console.error(err);
          })
      },

      //add passport country
      selectCountry: function () {
        addPassportCountry(this.selectedCountry, this.currentUser.primary_email)
          .then(() => {
            console.log('passport country added')
          })
          .catch((err) => {
            console.log(err)
          })
      },

      selectFitnessLevel: function () {
        setFitnessLevel(this.selectedFitnessLevel, this.currentUser.primary_email)
        .then(() => {
          console.log("Fitness level set");
        })
        .catch((err) => {
          console.log(err);
        })

      },

      addEmailAddress: function() {
        if (this.newEmail) {
          localStorage.currentUser.secondaryEmails.push(this.newEmail);
        }
      },

      saveButtonClicked: function() {
        editProfile(this.currentUser);
      },

      cancelButtonClicked: function() {
        alert("CANCELLED");
      }
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
