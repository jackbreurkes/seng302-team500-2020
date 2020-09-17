<template>
  <v-container>
    <v-row class="ma-0 pa-0">
      <h3>{{activity.activity_name}}</h3>
    </v-row>
    <v-row no-gutters v-if="!activity.continuous">
        <v-col><b>Starts</b></v-col>
        <v-col>{{formatDate(activity.start_time)}}</v-col>
    </v-row>
    <v-row no-gutters v-if="!activity.continuous">
        <v-col><b>Ends</b></v-col>
        <v-col>{{formatDate(activity.end_time)}}</v-col>
    </v-row>
    <v-row no-gutters>
        <v-col><b>Activity Types</b></v-col>
        <v-col>{{activity.activity_type.join(", ")}}</v-col>
    </v-row>
    <v-row no-gutters>
        <v-col><b>Your involvement</b></v-col>
        <v-col>{{creator ? "Creator" : (participating ? "Participating" : (following ? "Following" : "Not following"))}}</v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-btn v-on:click="$emit('clicked-goto-activity')" text small color="primary"><v-icon>mdi-arrow-right</v-icon>Go to activity</v-btn>
      </v-col>
    </v-row>
    <v-divider></v-divider>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import { describeDate, getIsFollowingActivity, getIsParticipating } from "../controllers/activity.controller"
import * as authService from '../services/auth.service';

// app Vue instance
const MapInfoWindowView = Vue.extend({
  name: "ActivitiesList",
  props: ["activity"],
  data: function() {
    return {
      following: false as boolean,
      participating: false as boolean,
      creator: false as boolean
    }
  },

  mounted: async function() {
    let myProfileId = authService.getMyUserId();
    if (myProfileId == null) {
      return;
    } else if (myProfileId == this.activity.creator_id) {
      this.creator = true;
    }
    getIsFollowingActivity(myProfileId, this.activity.activity_id)
      .then((booleanResponse) => {
        this.following = booleanResponse;
      })
    getIsParticipating(myProfileId, this.activity.activity_id)
      .then((booleanResponse) => {
        this.participating = booleanResponse;
      })
  },

  methods: {

    formatDate: function(dateString: string) {
      return describeDate(dateString);
    }

  }
});

export default MapInfoWindowView;
</script>

<style>
[v-cloak] {
  display: none;
}
p {
  display: inline-block;
}
</style>
