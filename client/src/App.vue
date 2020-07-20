<template>
  <div id="app">
    <v-app>

      <v-app-bar color="primary" dark app clipped-left:true-value="">
        <v-app-bar-nav-icon @click="burgerSelected" :color = this.burgerColour v-if="showNavBar()"></v-app-bar-nav-icon> 
        <v-toolbar-title>Intitulada</v-toolbar-title>
        <v-spacer></v-spacer>
        <div v-if="isLoggedIn">
          Logged in as {{currentName}} <v-btn @click="logoutButtonClicked" outlined>Logout</v-btn>
        </div>  
      </v-app-bar>
      <v-navigation-drawer 
          v-model= this.bar
          :expand-on-hover= this.collapsible
          :mini-variant = this.smallForm
          :right= this.right
          color = "rgba(30,30,30, 0.95)"
          absolute
          dark
          overlay-opacity= 0.7
          fixed
          permanent = true 
          v-if="showNavBar()"

        >
          <v-list
            dense
            nav
            class="py-0"
          >
            <v-list-item two-line :class="this.smallForm && 'px-0'">
              <v-list-item-avatar> 
                <img src="">
                <!-- this avatar component here makes a nice little barrier line, unable to replicate with other components -->
              </v-list-item-avatar>
  
              <v-list-item-content>
                <v-list-item-title>Application</v-list-item-title>
                <v-list-item-subtitle>Subtext</v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
  
            <v-divider></v-divider>
  
            <v-list-item
              v-for="item in items"
              :key="item.title"
              link
              @click="goTo(item.pathing)"
            >
              <v-list-item-icon>
                <v-icon>{{ item.icon }}</v-icon>
              </v-list-item-icon>
  
              <v-list-item-content>
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-navigation-drawer >
      <v-content           
      :class= "[computedPadding]">
        <transition name="page-transition">
          <router-view></router-view>
        </transition>
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
  import * as PropertiesService from './services/properties.service';
  import * as auth from "./services/auth.service";

  // app Vue instance
  const app = Vue.extend({
    name: 'app',
    // app initial state
    data: () => {
      return {
        burgerColour: 'white',
        currentProfileId: NaN as Number,
        bar: true,
        collapsible: true,
        smallForm: true,
        right: false,
        items: [] as any,
        isLoggedIn: false,
        currentName: ""
      }
    },
      computed: {
    computedPadding () {
      return `p${'l'}-${12}`
    },
  },
    
    created() {
    const profileId: number = parseInt(localStorage.getItem("userId")|| "Nan")
    this.currentProfileId = profileId;
    this.updateUserData();
    this.loadNavInfo();


    },
    watch: {
      $route() {
        this.updateUserData();
      }
    },

    methods: {
      loadNavInfo: function() {
          this.items = [ //USE https://materialdesignicons.com/ to find icons!!
            {title: 'Search for Users', icon: 'mdi-magnify', pathing:"/search/"},
            {title: 'Home ', icon: 'mdi-home', pathing:"/profiles/" + this.currentProfileId}, //sometimes replacing userId with NaN
            {title: 'Edit My Profile ', icon: 'mdi-cog', pathing:"/profiles/" + this.currentProfileId + "/edit"},
            {title: 'Logout', icon: 'mdi-logout', pathing:"LOGOUT"}, 
          ]
          if (auth.getMyPermissionLevel() >= 120) {
            this.items.push({title: 'Admin Dashboard', icon: 'mdi-account-cog', pathing:"/admin"}, )
          }

      },
      goTo: function(pathing : string) {
        if(!(pathing == "LOGOUT")){
          this.$router.push(pathing);
          }
        else {
          this.logoutButtonClicked();
        }
      },
      showNavBar: function(){
        //Dont display the nav bar side menu
        if(this.$route.path == "/login") {
          return false
        }
        return true
      },
      burgerSelected: function() {
        if (this.collapsible == true) {
        this.collapsible = false;
        this.smallForm = false;
        this.burgerColour = "grey"
        }
        else {
        this.collapsible = true;
        this.smallForm = true;
        this.burgerColour = "white"
        }
      },
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
          if (/*(!user || !user.firstname) && */auth.getMyPermissionLevel() >= 120 && PropertiesService.getAdminMode()) {
            this.currentName = "Admin";
          } else {
            this.currentName = user.nickname ? user.nickname : user.firstname + " " + user.lastname;
          }
        })
        .catch(() => {
          this.isLoggedIn = auth.getMyPermissionLevel() >= 120 && PropertiesService.getAdminMode();
          this.currentName = auth.getMyPermissionLevel() >= 120 && PropertiesService.getAdminMode() ? "Admin" : "";
        });
      }
    }
  });

  export default app
</script>

<style>
  [v-cloak] { display: none; }
</style>
