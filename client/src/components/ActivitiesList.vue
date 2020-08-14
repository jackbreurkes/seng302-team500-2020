<template>
  <div v-if="activities.length > 0">
    <v-card v-for="activity of activities" :key="activity.activity_id" class="mb-4" @click="goToActivity(activity.activity_id)">
      <ActivityCard :activityName="activity.activity_name" :activityLocation="activity.location" :activityDescription="activity.description" :isContinuous="activity.continuous" :activityStartTime="activity.start_time" :activityEndTime="activity.end_time" :activityType="activity.activity_type" :activityId="activity.activity_id" :creatorId="activity.creator_id" :authority="hasAuthority" ></ActivityCard>
    </v-card>
  </div>
  <div v-else>
    <p>this user has created no activities!</p>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import ActivityCard from "./ActivityCard.vue"
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from "../scripts/Activity";

// app Vue instance
const ActivitiesList = Vue.extend({
  name: "ActivitiesList",
  props: ["profileId", "authority", "activities", "myProfileId"], // props are passed in from the parent component
  components: { ActivityCard },

  // app initial state
  data: function() {
    return {
      hasAuthority: this.authority as boolean,
    };
  },

  watch: {
    // eslint-disable-next-line no-unused-vars
    authority(newValue, oldValue) {
      this.hasAuthority = newValue;
      this.$forceUpdate();
    }
  },

  methods: {
    
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
