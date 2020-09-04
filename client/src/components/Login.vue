<template>
  <div>
    <v-form v-model="isValid">
      <v-container class="fill-height" fluid>
          <v-row
            align="center"
            justify="center"
          >
            <v-col
            cols="12"
            sm="8"
            md="4"
            >
              <v-card class="elevation-12" width=100%>
                <v-toolbar
                  color="primary"
                  dark
                  flat
                >
                <v-toolbar-title>Login</v-toolbar-title>

                </v-toolbar>
                  <v-card-text>
                    <v-text-field v-model="email" label="Email address"></v-text-field>
                    <v-text-field v-model="password" label="Password" type="password" @keyup.enter.native="saveButtonClicked"></v-text-field>
                  </v-card-text>
                  <v-card-actions>
                    <p>New user?</p>
                    <router-link to="/register">
                        <p class="pl-1">Sign Up</p>
                    </router-link>
                    <p style="color: red;" class="pl-1">{{ errorMessage }}</p>
                    <v-spacer/>
                    <v-btn @click="saveButtonClicked" color="primary">Login</v-btn>
                  </v-card-actions>

              </v-card>
            </v-col>
          </v-row>
      </v-container>
    </v-form>



  </div>
</template>

<script lang="ts">
  import { submitForm } from '../controllers/login.controller'
  import Vue from 'vue'

  import * as auth from "../services/auth.service";

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
      this.goToCurrentUsersPageIfPossible();
    },

    methods: {
      saveButtonClicked() {
        submitForm({email: this.email, password: this.password})
          .then(() => {
            this.goToCurrentUsersPageIfPossible();
          })
          .catch((err: Error) => {
            this.errorMessage = err.message;
          })
        this.errorMessage = "submitting..."
      },

      /**
       * goes to the profile page associated with the currently logged in user
       * if no user info is stored currently, does nothing
       */
      async goToCurrentUsersPageIfPossible() {
        let storedUserIdIsValid = false;
        try {
          storedUserIdIsValid = await auth.verifyUserId();
        } catch (e) {
          console.error(e);
        }
        if (storedUserIdIsValid) {
          const myUserId: number | null = auth.getMyUserId();
          if (myUserId === null) {
            this.errorMessage = "failed to load user information. please log in again";
            return
          }
          this.errorMessage = "Logging you in...";
          if (!auth.isAdmin()) {
            this.$router.push({ name: "profilePage", params: {profileId: myUserId.toString()} })
              .catch((err) => {
                console.error(err);
                this.errorMessage = "failed to load profile page";
              });
          } else {
            this.$router.push({ name: "adminDashboard" });
          }
          
        }
        }
      }


  })

  export default Login
</script>

<style>
  [v-cloak] { display: none; }
  p {
    display: inline-block;
  }
</style>
