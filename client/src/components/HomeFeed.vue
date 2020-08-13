<template>
  <div id="Homefeed">
    <v-layout justify-center class="pt-1">
        <v-card width='900' height='650'>
            <v-toolbar color="primary" dark flat>
                <v-toolbar-title>Home Feed</v-toolbar-title>
            </v-toolbar>
        </v-card>
    </v-layout>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import * as HomefeedController from '../controllers/homefeed.controller';
// app Vue instance
const Homefeed = Vue.extend({
  name: "Homefeed",

  // app initial state
  data: function() {
    return {
    };
  },
  created: function(){
    HomefeedController.getHomeFeedData();
  },
  methods: {
  }
});

export default Homefeed;
</script>

<style>
[v-cloak] {
  display: none;
}

.mandatory {
  background-color: #ccf;
}

p {
    display: inline-block;
  }
</style>
