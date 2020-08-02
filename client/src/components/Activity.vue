<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="8">
          <v-card>
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title> {{ activity.activity_name }} </v-toolbar-title>
                <v-spacer></v-spacer>
                <div v-if="creatorId == currentProfileId">
                  <v-btn outlined class="mr-1"> Share </v-btn>
                  <v-btn @click="edit()" outlined class="mr-1"> Edit </v-btn>
                </div>
                <v-btn outlined class="mr-1"> Back </v-btn>
            </v-toolbar>

            <v-card-text class="grey lighten-4">
              <h3>Followers: {{ followerCount }} </h3>
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
                  <v-btn @click="toggleFollowingActivity" class="mr-1" color="primary" :disabled="participating"> {{ following ? "Unfollow" : "Follow" }} </v-btn>
                </div>
                <div>
                  <v-btn @click="toggleParticipation" class="mr-1" color="primary"> {{ participating ? "Unparticipate" : "Participate" }} </v-btn>
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

import { getActivity, followActivity, unfollowActivity, getIsFollowingActivity } from '../controllers/activity.controller';
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '../scripts/Activity';
import * as authService from '../services/auth.service';

// app Vue instance
const Activity = Vue.extend({
  name: "Activity",

  // app initial state
  data: function() {
    return {
      currentProfileId: NaN as number,
      activityId: NaN as number,
      creatorId: NaN as number,
      activity: [] as CreateActivityRequest,
      followerCount: -1,
      participating: false,
      following: false
    };
  },

  created() {
    let myProfileId = authService.getMyUserId();
    if (myProfileId == null) {
      this.$router.push('login');
    } else {
      this.currentProfileId = myProfileId;
    }

    const activityId: number = parseInt(this.$route.params.activityId);
    const creatorId: number = parseInt(this.$route.params.profileId);
    if (isNaN(creatorId) || isNaN(activityId)) {
      this.$router.back();
    } else {
      this.activityId = activityId;
      this.creatorId = creatorId;

      getActivity(creatorId, activityId)
      .then((res) => {
        this.activity = res;
      })
      .catch(() => {
        this.$router.back();
      });

      getIsFollowingActivity(this.currentProfileId, this.activityId)
      .then((following) => {
        this.following = following;
      })
    }
},

  methods: {
    edit: function() {
      this.$router.push(
        `/profiles/${this.creatorId}/editActivity/${this.activityId}`
      );
    },
    toggleFollowingActivity: function() {
      if (this.following) {
        unfollowActivity(this.currentProfileId, this.activityId)
        .then(() => {
          console.log("The unfollow functionality has not been implemented yet.")
        })
        .catch((err) => {
          console.log(err)
        })
      } else {
        followActivity(this.currentProfileId, this.activityId)
        .then(() => {
          console.log("Follow functionality has not been implemented yet.")
        })
        .catch((err) => {
          console.log(err)
        })
      }
    },
    toggleParticipation: function() {
      console.log("Participation functionality has not yet been implemented.")
    },
    getFollowingStatus: async function() {
      getIsFollowingActivity(this.currentProfileId, this.activityId)
      .then((following) => {
        this.following = following;
      })
    }
  }

});

export default Activity;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
