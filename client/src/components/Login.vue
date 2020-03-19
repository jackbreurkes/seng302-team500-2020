<template>
  <div>
    <v-form v-model="isValid">
      <v-container fluid>
        <v-col
        cols="12"
        sm="6"
        >
          <router-link to="/register">
            <p>this is link to the register page</p>
          </router-link>
          <h1>This is the login page</h1>
          <v-text-field v-model="email" label="email" outlined rounded dense filled background-color=#c4daff></v-text-field>
          <v-text-field v-model="password" label="password" type="password" outlined rounded dense filled background-color=#c4daff></v-text-field>
          <v-btn @click="saveButtonClicked">Save</v-btn>
          <p>{{ errorMessage }}</p>
        </v-col>
      </v-container>
    </v-form>
    


  </div>
</template>

<script lang="ts">
  import { tagMandatoryAttributes } from '../scripts/LoginRegisterHelpers'
  import { submitForm } from '../controllers/login.controller'
  import Vue from 'vue'

  // app Vue instance
  const Login = Vue.extend({
    name: 'Login',

    // app initial state
    data: function() {
      return {
        isValid: false,
        mandatoryAttributes: ['email', 'password'],
        email: "",
        password: "",
        errorMessage: ""
      }
    },

    mounted: function() {
      let refs = this.$refs;
      tagMandatoryAttributes(refs, this.mandatoryAttributes);
    },

    methods: {
      saveButtonClicked() {
        submitForm({email: this.email, password: this.password})
          .then(() => {
            this.$router.push({ name: "profilePage" })
              .catch((err) => {
                console.error(err);
                this.errorMessage = "failed to load profile page";
              })
          })
          .catch((err: Error) => {
            this.errorMessage = err.message;
          })
        this.errorMessage = "submitting..."
      }
    }


  })

  export default Login
</script>

<style>
  [v-cloak] { display: none; }
</style>
