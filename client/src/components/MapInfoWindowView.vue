<template>
  <v-container>
    <v-row class="ma-0 pa-0">
      <h3>{{activity.activity_name}}</h3>
    </v-row>
    <v-row no-gutters v-if="!activity.continuous">
        <v-col><strong>Starts</strong></v-col>
        <v-col>{{formatDate(activity.start_time)}}</v-col>
    </v-row>
    <v-row no-gutters v-if="!activity.continuous">
        <v-col><strong>Ends</strong></v-col>
        <v-col>{{formatDate(activity.end_time)}}</v-col>
    </v-row>
    <v-row no-gutters>
        <v-col><strong>Activity Types</strong></v-col>
        <v-col>{{activity.activity_type.join(", ")}}</v-col>
    </v-row>
    <v-row no-gutters v-if="role !== null">
        <v-col><strong>Your involvement</strong></v-col>
        <v-col>{{role}}</v-col>
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
import { describeDate, getIsFollowingActivity, getIsParticipating, getIsOrganising } from "../controllers/activity.controller"
import * as authService from '../services/auth.service';

// app Vue instance
const MapInfoWindowView = Vue.extend({
  name: "MapInfoWindowView",
  props: ["activity"],
  data: function() {
    return {
      following: false as boolean,
      organising: false as boolean,
      participating: false as boolean,
      creator: false as boolean
    }
  },

  mounted: async function() {
    let myProfileId = authService.getMyUserId();
    if (myProfileId == null) {
      return;
    }
    if (myProfileId == this.activity.creator_id) {
      this.creator = true;
    }
    getIsFollowingActivity(myProfileId, this.activity.activity_id)
      .then((isFollowing) => {
        this.following = isFollowing;
      })
    getIsOrganising(myProfileId, this.activity.activity_id)
      .then((isOrganising) => {
        this.organising = isOrganising;
      })
    getIsParticipating(myProfileId, this.activity.activity_id)
      .then((isParticipating) => {
        this.participating = isParticipating;
      })
  },

  methods: {
    formatDate: function(dateString: string) {
      return describeDate(dateString);
    }
  },

  computed: {
    role: function() {
      if (this.creator) {
        return "Creator";
      } else if (this.organising) {
        return "Organiser";
      } else if (this.participating) {
        return "Participating";
      } else if (this.following) {
        return "Following";
      } else {
        return null;
      }
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
