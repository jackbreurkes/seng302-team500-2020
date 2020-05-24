<template>
  <div id="AdminDashboard">
    <v-form ref="adminDashboardForm">
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center">
          <v-col cols="12" sm="8" md="4">
            <v-card class="elevation-12" width="100%">
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
              <br /><br /><br /><br /><br /><br /><br /><br /><br />
              You are logged in as an admin
              <br /><br /><br /><br /><br /><br /><br /><br /><br />
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

// app Vue instance
const AdminDashboard = Vue.extend({
  name: "AdminDashboard",

  // app initial state
  data: function() {
    return {
      adminToggleMode: 0
    };
  },

  mounted: function() {
    this.adminToggleMode = this.getCookie("adminMode")=="true" ? 0 : 1;
  },

  methods: {
    setAdminMode: function() {
      if (this.adminToggleMode == 0) {
        document.cookie = "adminMode=true";
      } else {
        document.cookie = "adminMode=false";
      }
    },
    getCookie: function(name: string) {
      let value = `; ${document.cookie}`;
      let parts = value.split(`; ${name}=`);
      if (parts && parts.length === 2) {
        let cookieEnd = parts.pop();
        if (cookieEnd !== undefined) {
          return cookieEnd.split(';').shift()
        }
      }
      return null;
    }
  }


});

export default AdminDashboard;
</script>
