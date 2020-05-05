<template>
  <div v-if="activities.length > 0">
    
    <v-card v-for="activity of activities" :key="activity.activity_id">
        <v-card-title>
            {{ activity.activity_name }}
            <v-spacer></v-spacer>
            <v-chip class = "ml-2" v-if="activity.creator_id === profileId">Creator</v-chip>
        </v-card-title>
        <v-card-text>
                <p>
                    {{ activity.continuous ? 'continuous' : 'duration' }}
                </p>
                <v-spacer></v-spacer>
                <v-chip
                      class="ml-2 mb-2"
                      v-for="activityType of activity.activity_type"
                      v-bind:key="activityType"
                    >{{ activityType }}</v-chip>
        </v-card-text>
    </v-card>

  </div>
  <div v-else>
      <p>this user has created no activities!</p>
  </div>
</template>

<script lang="ts">
  import Vue from 'vue'
  import * as activityController from '../controllers/activity.controller';
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '../scripts/Activity';

  // app Vue instance
  const ActivitiesList = Vue.extend({
    name: 'ActivitiesList',
    props: ['profileId'], // props are passed in from the parent component

    // app initial state
    data: function() {
      return {
        isValid: false,
        mandatoryAttributes: ['email', 'password'],
        email: "",
        password: "",
        errorMessage: "",
        activities: [] as CreateActivityRequest[],
      }
    },

    created() {
        activityController.getActivitiesByCreator(this.profileId)
        .then((res) => this.activities = res)
        .catch((err) => {
            console.error(err);
        });
    },

    methods: {
      
    }


  })

  export default ActivitiesList
</script>

<style>
  [v-cloak] { display: none; }
  p {
    display: inline-block;
  }
</style>
