<template>
  <div id="app">
    <v-app>
      <v-app-bar color="primary" dark app>
        <v-toolbar-title>WE STILL DON'T HAVE A NAME</v-toolbar-title>
        <v-spacer></v-spacer>
        <div v-if="isLoggedIn">
          Logged in as {{currentName}} <v-btn @click="logoutButtonClicked" color="secondary">Logout</v-btn>
        </div>  
      </v-app-bar>
      <v-content>
        <router-view></router-view>
      </v-content>
    </v-app>
  </div>
</template>

<script lang="ts">
  import Vue from "vue"
  import {
    logoutCurrentUser,
    fetchCurrentUser
  } from "./controllers/profile.controller";
  // app Vue instance
  const app = Vue.extend({
    name: 'app',
    // app initial state
    data: () => {
      return {
        isLoggedIn: false,
        currentName: ""
      }
    },
    created() {
      this.updateUserData();
    },
    watch: {
      $route() {
        this.updateUserData();
      }
    },

    methods: {
      logoutButtonClicked: function() {
        logoutCurrentUser()
          .then(() => {
            this.$router.push({ name: "login" });
          })
          .catch((err: any) => {
            console.error(err);
            this.$router.push({ name: "login" });
          });
      },
      updateUserData: function() {
        fetchCurrentUser().then((user) => {
          this.isLoggedIn = true;
          this.currentName = user.nickname ? user.nickname : user.firstname + " " + user.lastname;
        })
        .catch(() => {
          this.isLoggedIn = false;
        });
      }
    }
  });

  export default app
</script>

<style>
  [v-cloak] { display: none; }
</style>
