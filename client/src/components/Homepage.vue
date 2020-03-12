<template>
  <div>
    <h1>Homepage</h1>
      <p>Homepage</p>
      <p>First Name: {{ currentUser.firstName }}</p>
      <p>Last Name: {{ currentUser.lastName }} </p>

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
      <p>Primary email: {{ currentUser.primary_email }}</p>


      <p>Secondary Emails:</p>
      <ul>
        <li v-for="email in currentUser.additional_email" :key="email">{{ email }}</li>
      </ul>

      <!-- New Email input field and button -->
      <template v-if="currentUser.additional_email && currentUser.additional_email.length < 5">
        <input ref="newEmail" id="newEmail" type="email" v-model="newEmail" />
        <button id="addEmailAddress" @click="addEmailAddress">Add Email</button>
      </template>

      

      <br>
      <label>Enter Old Password <input ref="oldPassword" id="oldPassword" type="password" v-model="oldPassword" /></label>
      <label>Enter New Password <input ref="newPassword" id="newPassword" type="password" v-model="newPassword" /></label>
      <label>Repeat New Password <input ref="repeatPassword" id="repeatPassword" type="password" v-model="repeatPassword" /></label>
      <button id="updatePassword" @click="updatePassword">Update your password</button>

      <br>
      <button @click="logoutButtonClicked">Logout</button>
  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import { UserApiFormat } from '../scripts/User';
  import { logoutCurrentUser, addPassportCountry, fetchCurrentUser, setFitnessLevel, addEmail } from '../controllers/profile.controller'

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
        oldPassword: "",
        newPassword: "",
        repeatPassword: "",
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
      updatePassword: function(){
        updatePassword(this.oldPassword,this.newPassword,this.repeatPassword)
          .then(() => {
            this.$router.push({ name: "updatePassword" })
          })
          .catch((err) => {
            console.error(err);
          })

      },
      //click logout button
      logoutButtonClicked: function() {
        logoutCurrentUser()
          .then(() => {
            console.log("updatePassword")
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
        addEmail(this.newEmail)
        .then(() => {
          console.log("Email address added");
        })
        .catch((err) => {
          console.log(err);
        })
      },
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
