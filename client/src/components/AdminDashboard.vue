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
import Search from "./Search.vue"

// app Vue instance
const AdminDashboard = Vue.extend({
  name: "AdminDashboard",
  components: { Search },

  // app initial state
  data: function() {
    return {}
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
