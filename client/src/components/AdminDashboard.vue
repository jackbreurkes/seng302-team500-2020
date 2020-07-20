<template>
  <div id="AdminDashboard">
    <v-form ref="adminDashboardForm">
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center">
          <v-col cols="12">
            <v-card class="elevation-12">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>Admin Dashboard</v-toolbar-title>
              </v-toolbar>
              <h4>Admin mode:</h4>
              <v-btn-toggle v-model="adminToggleMode" mandatory @change="setAdminMode">
                <v-btn style="width: 15px">
                  on
                </v-btn>
                <v-btn style="width: 15px">
                  off
                </v-btn>
      
              </v-btn-toggle>

              <Search ref="searchSubcomponent"></Search>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import * as PropertiesService from '../services/properties.service';
import Search from "./Search.vue"

// app Vue instance
const AdminDashboard = Vue.extend({
  name: "AdminDashboard",
  components: { Search },

  // app initial state
  data: function() {
    return {
      adminToggleMode: 0
    };
  },

  mounted: function() {
    this.adminToggleMode = PropertiesService.getAdminMode() ? 0 : 1;
  },

  methods: {
    setAdminMode: function() {
      if (this.adminToggleMode == 0) {
        PropertiesService.setAdminMode(true);
        history.go(0)
      } else {
        PropertiesService.setAdminMode(false);
        history.go(0)
      }
    },
    
  },

  beforeRouteLeave(to, from, next) {
    let searchSub: any = this.$refs.searchSubcomponent;
    searchSub.prepareToExit(to);
    next();
  }


});

export default AdminDashboard;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
