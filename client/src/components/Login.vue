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
    
    <button @click="submitForm">Save</button>

    <p>{{ errorMessage }}</p>
  </div>
</template>

<script lang="ts">
  import { tagMandatoryAttributes, login } from '../scripts/login-register-helpers'
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
      async submitForm() {
        let userInfo = await login(this.email, this.password);

        if (userInfo === null) {
          this.errorMessage = "no matching user found";
        } else {
          this.errorMessage = "";
          this.$router.push({ name: "profilePage" })
        }
      }
    }


  })

  export default Login
</script>

<style>
  [v-cloak] { display: none; }
</style>
