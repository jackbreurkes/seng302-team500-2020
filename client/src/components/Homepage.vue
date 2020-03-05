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
      <button @click="logout">Logout</button>
  </div>
</template>

<script>

  // app Vue instance
  const Homepage = {
    name: 'Homepage',
    
    // app initial state
    data: function() {
      return {
        currentUser: {},
        passportCountries: [],
        selectedCountry: "",
        selectedFitnessLevel: 0
      }
    },

    created() {
      this.currentUser = JSON.parse(localStorage.currentUser);
      this.selectedFitnessLevel = this.currentUser.fitnessLevel || 0

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

      logout: function() {
        localStorage.removeItem("currentUser")
        this.$router.push({ name: "initialPage" })
      },
      selectCountry: function () {
        if (this.currentUser.passports === undefined) {
          this.currentUser.passports = []
        }

        const countryName = this.selectedCountry.name || null
        if (countryName === null) {
          return
        }

        if (this.currentUser.passports.includes(countryName)) {
          alert("you already have this as a passport country")
          return;
        }

        this.currentUser.passports.push(countryName)
        localStorage.currentUser = JSON.stringify(this.currentUser)

        let users = JSON.parse(localStorage.users)
        for (let index = 0; index < users.length; index++) {
          if (users[index].email === this.currentUser.email) {
            users.splice(index, 1, this.currentUser)
          }
        }
        localStorage.users = JSON.stringify(users)

      },

      selectFitnessLevel: function () {
        const selection = this.selectedFitnessLevel;
        if (this.currentUser.fitnessLevel !== selection) {
          alert("Set fitness level to " + selection)
          this.currentUser.fitnessLevel = selection
        }
        localStorage.currentUser = JSON.stringify(this.currentUser)

        let users = JSON.parse(localStorage.users)
        for (let index = 0; index < users.length; index++) {
          if (users[index].email === this.currentUser.email) {
            users.splice(index, 1, this.currentUser)
          }
        }
        localStorage.users = JSON.stringify(users)

      }
    }
  }

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
