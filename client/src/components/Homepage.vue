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
      <p>Primary email: {{ currentUser.primaryEmail }}</p>
      <p>All emails:</p>
      <ul>
        <li v-for="email in currentUser.secondaryEmails" :key="email">{{ email }}</li>
      </ul>

      <br>
      <button @click="logoutButtonClicked">Logout</button>
  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import User, { UserInterface, UserBuilder } from '../scripts/User'
  import { logoutCurrentUser, addPassportCountry, fetchCurrentUser, setFitnessLevel } from '../controllers/profile.controller'

  // app Vue instance
const Homepage =  Vue.extend({
    name: 'Homepage',
    
    // app initial state
    data: function() {
      return {
        currentUser: {} as User,
        passportCountries: [],
        selectedCountry: "" as any,
        selectedFitnessLevel: 0
      }
    },

    created() {
      fetchCurrentUser()
        .then((user) => {
          this.currentUser = user;
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

      logoutButtonClicked: function() {
        logoutCurrentUser()
          .then(() => {
            this.$router.push({ name: "initialPage" })
          })
          .catch((err) => {
            console.error(err);
          })
      },

      selectCountry: function () {
        addPassportCountry(this.selectedCountry, this.currentUser.primaryEmail)
          .then(() => {
            console.log('passport country added')
          })
          .catch((err) => {
            console.log(err)
          })
      },

      selectFitnessLevel: function () {
        setFitnessLevel(this.selectedFitnessLevel, this.currentUser.primaryEmail)
        .then(() => {
          console.log("Fitness level set");
        })
        .catch((err) => {
          console.log(err);
        })

      }
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
