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

      <form submit="false">
      <label for="lastname">lastname</label>
      <input ref="lastname" id="lastname" type="text" v-model="lastName" />
      <label for="firstname">firstname</label>
      <input ref="firstname" id="firstname" type="text" v-model="firstName" />
      <label for="middlename">middlename</label>
      <input ref="middlename" id="middlename" type="text" v-model="middleName" />
      <label for="nickname">nickname</label>
      <input ref="nickname" id="nickname" type="text" v-model="nickname" />
      <label for="bio">bio</label>
      <input ref="bio" id="bio" type="text" v-model="bio" />
      <label for="date-of-birth">date of birth</label>
      <input ref="date-of-birth" id="date-of-birth" type="date" v-model="dateOfBirth" />

      <label for="gender">Gender:</label>
      <select ref="gender" id="gender" v-model="gender">
        <option disabled value>Please select a gender...</option>
        <option value="male">Male</option>
        <option value="female">Female</option>
        <option value="non-binary">Non-binary</option>
      </select>
    </form>


  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import { UserApiFormat} from '../scripts/User'
  import { logoutCurrentUser, addPassportCountry, fetchCurrentUser, setFitnessLevel } from '../controllers/profile.controller'

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

      focusField(name){
          this.currentUser.firstname = name;
      },

      blurField(){
        this.currentUser.firstname = '';
      },

      showField(name){
        return (this.currentUser.firstname == '' || this.currentUser.firstname == name)
      },

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
      }
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
