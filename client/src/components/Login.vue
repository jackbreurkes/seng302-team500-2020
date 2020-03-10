<template>
  <div>
    <router-link to="/register">
      <p>this is link to the register page</p>
    </router-link>
    <h1>This is the login page</h1>
    <form submit="false">
      <label for="email">email</label>
      <input ref="email" id="email" type="email" v-model="email">
      <label for="password">password</label>
      <input ref="password" id="password" type="password" v-model="password">

    </form>

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
