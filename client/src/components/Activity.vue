<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="8">
          <v-card>
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title> {{ activity.activity_name }} </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-btn outlined class="mr-1"> Share </v-btn>
                <v-btn outlined class="mr-1"> Edit </v-btn>
                <v-btn outlined class="mr-1"> Back </v-btn>
            </v-toolbar>

            <v-card-text class="grey lighten-4">
              <h3>Followers: {{ follower_count }} </h3>
              <br>
              <h3>Activity Information</h3>
              <br>
              <p> Description: {{ activity.description }} </p>
              <br>
              <p> Location: {{ activity.location }} </p>

              <div v-if="activity.duration == 1"> <!-- Activity has a start and end time -->
                <p> Starts at: {{ activity.start_time_string }} </p>
                <br>
                <p> Ends at: {{ activity.end_time_string }} </p>
              </div>
              <div v-else>
                <p> Activity is continous and ongoing </p>
              </div>

              <v-chip
                class="mr-2 mb-2"
                v-for="activityType of activity.activity_type"
                v-bind:key="activityType"
                outlined
              >{{ activityType }}</v-chip>  

              <br><v-divider></v-divider><br>

              <p> Please insert a UserRoleList component here! </p>

              <br><v-divider></v-divider><br>

              <v-card-actions>
                <v-spacer></v-spacer>
                <div>
                  <v-btn class="mr-1" color="primary" :disabled="participating"> {{ following ? "Unfollow" : "Follow" }} </v-btn>
                </div>
                <div>
                  <v-btn class="mr-1" color="primary"> {{ participating ? "Unparticipate" : "Participate" }} </v-btn>
                </div>
              </v-card-actions>

              </v-card-text>
            <v-row>
            </v-row>

          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

// app Vue instance
const Activity = Vue.extend({
  name: "Activity",

  // app initial state
  data: function() {
    return {
      activity: {"activity_name": "Easter Egg Hunt 2.0", "description": "Exactly what it sounds like", "duration": 1, "start_time_string": "", "end_time_string": "", "activity_type": ["Easter", "Chocolate", "Walking", "Family-Friendly"]},
      participating: true,
      following: true
    };
  }

});

export default Activity;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
