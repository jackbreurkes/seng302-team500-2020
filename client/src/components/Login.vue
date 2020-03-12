<template>
  <div>
    <router-link to="/register">
      <p>this is link to the register page</p>
    </router-link>
    <h1>This is the login page</h1>
    <v-form v-model="isValid">
      <v-text-field
        v-model="email"
        label="email"></v-text-field>
      <v-text-field
        v-model="password"
        label="password"
        type="password"></v-text-field>
    </v-form>
    
    <button @click="saveButtonClicked">Save</button>

    <p>{{ errorMessage }}</p>
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
