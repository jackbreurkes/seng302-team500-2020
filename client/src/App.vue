<template>
  <div id="app">
    <v-app>
      <v-app-bar color="primary" dark app clipped-left:true-value style="z-index: 1000;">
        <v-app-bar-nav-icon @click="burgerSelected" :color="this.burgerColour" v-if="showNavBar()"></v-app-bar-nav-icon>
        <v-toolbar-title @click="routeToProfile()" onmouseover="" style="cursor: pointer;">Intitulada</v-toolbar-title>
        <v-img @click="routeToProfile()" onmouseover="" style="cursor: pointer;" max-height="80" max-width="80" src="../public/naviconlogo.png"></v-img>

        <v-spacer></v-spacer>
        <div v-if="isLoggedIn">
          Logged in as {{currentName}} <v-btn @click="logoutButtonClicked" outlined>Logout</v-btn>
        </div>
      </v-app-bar>
      <v-navigation-drawer
        :expand-on-hover="this.collapsible"
        :mini-variant="this.smallForm"
        color="rgba(30,30,30, 0.95)"
        dark
        overlay-opacity="0.7"
        fixed
        style="z-index: 999;"
        permanent
        v-if="showNavBar()"
      >
        <v-list
          dense
          nav
          class="py-0"
        >
          <v-list-item two-line :class="this.smallForm && 'px-0'">
            <v-list-item-avatar>
              <!-- this avatar component here makes a nice little barrier line, unable to replicate with other components -->
            </v-list-item-avatar>

            <v-list-item-content>
              <v-list-item-title>Application</v-list-item-title>
              <v-list-item-subtitle>Subtext</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>

          <v-divider></v-divider>
          


          <!-- map toggle -->
          <v-list-item
              link
              @click="setDisplayMap(!displayMap)"
              :class="displayMap ? '' : 'mb-12'"
            >
            <v-list-item-icon>
              <v-icon>{{ displayMap ? "mdi-map-minus" : "mdi-map-search" }}</v-icon>
            </v-list-item-icon>

            <v-list-item-content>
              <v-list-item-title>{{ displayMap ? "Hide Map" : "Show Map" }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>

          <!-- split direction toggle -->
          <v-list-item
              v-if="displayMap"
              link
              @click="setHorizontalSplit(!horizontalSplit)"
            >
            <v-list-item-icon>
              <v-icon>{{ horizontalSplit ? "mdi-view-split-horizontal" : "mdi-view-split-vertical" }}</v-icon>
            </v-list-item-icon>

            <v-list-item-content>
              <v-list-item-title>Change Map Orientation</v-list-item-title>
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
      :class="showNavBar() ? 'nav-drawer-margin' : ''">
        <!-- SplitPanes API: https://antoniandre.github.io/splitpanes/ -->
        <splitpanes
            class="default-theme"
            :horizontal="horizontalSplit"
            @resize="mapPaneSize = 100 - $event[0].size"
            @pane-maximize="displayMap = true"
        >
          <pane id="main-pane" :size="100 - mapPaneSize" min-size="20">
            <transition name="page-transition">
              <router-view :key="$route.path"></router-view>
            </transition>
          </pane>
          <pane v-if="showNavBar()" id="map-pane" :size="mapPaneSize">
            <MapView id="map-view"></MapView>
          </pane>
        </splitpanes>
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
import * as auth from "./services/auth.service";
import * as preferences from "./services/preferences.service"

import { Splitpanes, Pane } from "splitpanes";
import MapView from "./components/MapView.vue";
import "splitpanes/dist/splitpanes.css";

  // app Vue instance
  const app = Vue.extend({
    name: 'app',
    components: { Splitpanes, Pane, MapView },
    // app initial state
    data: () => {
      return {
        burgerColour: 'white',
        bar: true,
        collapsible: true,
        smallForm: true,
        right: false,
        items: [] as any,
        isLoggedIn: false,
        currentName: "",
        displayMap: false,
        mapPaneSize: 0,
        horizontalSplit: true,
      }
    },
    
    created() {
      this.updateUserData();
      this.updateNavInfo();
      this.horizontalSplit = preferences.getPrefersHorizontalSplit();

      this.$root.$on('mapPaneToggle', (isOpen: boolean) => {
        this.setDisplayMap(isOpen)
      })
    },

    watch: {
      $route() {
        this.updateUserData();
      },
      mapPaneSize: function(newVal) {
        this.displayMap = newVal > 0.1;
      }
    },

    methods: {
      /**
       * sets whether the map pane should be displayed. 
       */
      setDisplayMap(display: boolean) {
        if (display) {
          this.mapPaneSize = 30;
        } else {
          this.mapPaneSize = 0.00001; // setting to zero doesn't work, no idea why
        }
      },

      /**
       * sets whether the screen should be split horizontally.
       */
      setHorizontalSplit(horizontal: boolean) {
        this.horizontalSplit = horizontal;
        preferences.setPrefersHorizontalSplit(horizontal);
      },

      updateNavInfo: function() {
          this.items = [ //USE https://materialdesignicons.com/ to find icons!!
            {title: 'Home Feed', icon: 'mdi-home', pathing: "/homefeed" },
            {title: 'Create an Activity', icon: 'mdi-plus', pathing:"/profiles/" + auth.getMyUserId() + "/createActivity" },
            {title: 'Search for Activities', icon: 'mdi-bike', pathing:"/activities/"},
            {title: 'Search for Users', icon: 'mdi-magnify', pathing:"/search/"},
            {title: 'My Profile ', icon: 'mdi-account', pathing:"/profiles/" + auth.getMyUserId()},
            {title: 'Edit My Profile ', icon: 'mdi-cog', pathing:"/profiles/" + auth.getMyUserId() + "/edit"},
            {title: 'Logout', icon: 'mdi-logout', pathing:"LOGOUT"},
          ]
          
          if (auth.isAdmin()) {
            this.items.push({title: 'Admin Dashboard', icon: 'mdi-account-cog', pathing:"/admin"}, )
          }
          

      },

      routeToProfile: function() {
        this.$router.push('/profiles/' + auth.getMyUserId());
      
      },

      goTo: function(pathing : string) {
        this.updateNavInfo(); // updates user id information in path links
        if(!(pathing == "LOGOUT")){
          this.$router.push(pathing).catch(() => {}); // ignore routing errors from navbar
          }
        else {
          this.logoutButtonClicked();
        }
      },
      showNavBar: function(){
        //Dont display the nav bar side menu
        if(this.$route.path == "/login" || this.$route.path == '/register'|| this.$route.path == '/') {
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
        this.displayMap = false;
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
          if (auth.isAdmin()) {
            this.currentName += " (Admin)";
          }
          this.updateNavInfo()
        })
        .catch(() => {
          this.isLoggedIn = auth.isAdmin();
          this.currentName = auth.isAdmin() ? "Admin" : "";
        });
      }
    }
  });

  export default app
</script>

<style>
[v-cloak] {
  display: none;
}

.nav-drawer-margin {
  margin-left: 56px; 
}

.splitpanes {
  height: calc(100vh - 64px); /* navbar is 64px */
}

body {
  overflow-y: hidden;
}

.splitpanes__pane {
  overflow-y: auto;
  overflow-x: hidden;
}

#map-view {
  height: 100%;
  width: 100%;
}
</style>
