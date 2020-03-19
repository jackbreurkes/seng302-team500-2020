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
                <v-toolbar-title>Login:</v-toolbar-title>
                
                </v-toolbar>
                  <v-card-text>
                    <p>New Here?</p>
                    <router-link to="/register">
                      <p>Register</p>
                    </router-link>
                  </v-card-text>
                  <v-card-text>
                    <v-text-field v-model="email" label="email" dense filled background-color=#c4daff m></v-text-field>
                    <v-text-field v-model="password" label="password" type="password" dense filled background-color=#c4daff ></v-text-field>
                  </v-card-text>
                  <v-card-actions>
                    <p>{{ errorMessage }}</p>
                    <v-spacer/>
                    <v-btn @click="saveButtonClicked">Save</v-btn>
                  </v-card-actions>
                  
              </v-card>
            </v-col>
          </v-row>
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
  p {
    display: inline-block;
  }
</style>
