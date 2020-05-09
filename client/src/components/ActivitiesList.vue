<template>
  <div v-if="activities.length > 0">
    <v-card v-for="activity of activities" :key="activity.activity_id" class="mb-2">
      <v-card-title>
        {{ activity.activity_name }} ({{ activity.location }})
        <v-spacer></v-spacer>
        <v-chip class="ml-2" @click="editActivity(`${activity.activity_id}`)" v-if="hasAuthority">Edit</v-chip>
        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <div v-on="on">
              <v-chip class="ml-2" v-if="activity.creator_id === profileId" outlined>Creator</v-chip>
            </div>
          </template>
          <span>Your role</span>
        </v-tooltip>
      </v-card-title>

      <v-card-text>
        <span class="subtitle-1">{{ activity.description }}</span>
        <br />
        <p
          class="body-2"
        >{{ activity.continuous ? 'continuous' : getDurationDescription(activity.start_time, activity.end_time) }}</p>
        <v-spacer></v-spacer>
        <v-chip
          class="mr-2 mb-2"
          v-for="activityType of activity.activity_type"
          v-bind:key="activityType"
          outlined
        >{{ activityType }}</v-chip>
      </v-card-text>
    </v-card>
  </div>
  <div v-else>
    <p>this user has created no activities!</p>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import * as activityController from "../controllers/activity.controller";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from "../scripts/Activity";

// app Vue instance
const ActivitiesList = Vue.extend({
  name: "ActivitiesList",
  props: ["profileId", "authority"], // props are passed in from the parent component

  // app initial state
  data: function() {
    return {
      hasAuthority: this.authority as boolean,
      isValid: false,
      mandatoryAttributes: ["email", "password"],
      email: "",
      password: "",
      errorMessage: "",
      activities: [] as CreateActivityRequest[]
    };
  },

  watch: {
    // eslint-disable-next-line no-unused-vars
    authority(newValue, oldValue) {
      this.hasAuthority = newValue;
      this.$forceUpdate();
    }
  },

  created() {
    activityController
      .getActivitiesByCreator(this.profileId)
      .then(res => (this.activities = res))
      .catch(err => {
        console.error(err);
      });
  },

  methods: {
    getDurationDescription(startTime: string, endTime: string): string {
      return activityController.describeDurationTimeFrame(startTime, endTime);
    },
    editActivity(activityId: number) {
      this.$router.push(`/profiles/${this.profileId}/editActivity/${activityId}`);
    }
  }
});

export default ActivitiesList;
</script>

<style>
[v-cloak] {
  display: none;
}
p {
  display: inline-block;
}
</style>
