<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="8">
          <v-card>
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title>
                <v-btn
                  dark
                  icon
                  @click="backButtonClicked"
                  >
                  <v-icon>mdi-arrow-left</v-icon>
                </v-btn>
                {{ activity.activity_name }} 
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <div>
                  <v-chip v-if="currentProfileId === creatorId" outlined>Creator</v-chip>
                  <v-chip v-if="organiser" outlined>Organiser</v-chip>
                  <v-chip v-if="following" outlined>Following</v-chip>
                  <v-chip v-if="participating" outlined>Participating</v-chip>
                  <v-menu v-if="currentProfileId === creatorId || organiser" bottom left offset-y>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        dark
                        icon
                        v-bind="attrs"
                        v-on="on"
                      >
                        <v-icon>mdi-dots-vertical</v-icon>
                      </v-btn>
                    </template>

                    <v-list>
                      <v-list-item
                        @click="edit()"
                      >
                        <v-list-item-title>Edit Activity</v-list-item-title>
                      </v-list-item>
                      <v-list-item v-if="currentProfileId === creatorId" @click="confirmDeleteModal = true">
                        <v-dialog v-model="confirmDeleteModal" width="290">
                          <template v-slot:activator="{ on }">
                            <v-list-item-title v-on="on">Delete Activity</v-list-item-title>
                          </template>
                          <v-card>
                            <v-card-title class="headline" primary-title>Delete activity?</v-card-title>
                            <v-card-text>This operation cannot be undone.</v-card-text>
                            <v-divider></v-divider>
                            <v-card-actions>
                              <v-btn text @click="confirmDeleteModal = false">Cancel</v-btn>
                              <v-spacer></v-spacer>
                              <v-btn color="error" text @click="deleteActivity">Delete</v-btn>
                            </v-card-actions>
                          </v-card>
                        </v-dialog>
                      </v-list-item>
                    </v-list>
                  </v-menu>
                </div>
            </v-toolbar>

            <v-card-text class="grey lighten-4">
              <h3>Activity Information</h3>
              <br>
              <p> Description: {{ activity.description }} </p>
              <br>
              <p> Location: {{ activity.location }} </p>

              <div v-if="hasTimeFrame(activity)"> <!-- Activity has a start and end time -->
                <p> {{ getDurationDescription(activity.start_time, activity.end_time) }} </p>
              </div>
              <div v-else>
                <p> Activity is continuous </p>
              </div>

              <v-chip
                class="mr-2 mb-2"
                v-for="activityType of activity.activity_type"
                v-bind:key="activityType"
                outlined
              >{{ activityType }}</v-chip> 
              <br>

              <v-divider></v-divider><br>

              <v-data-table
                  :no-data-text="noDataText"
                  :headers="headers"
                  :items="users"
                  item-key="profile_id"
                  @click:row="goToUser"
                  single-select
                  v-model="selectedUsers"
              >
                <template #item.full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }}</template>
                <!-- <template #item.full_name="{ item }">{{ item.firstname }} {{ item.userId }}{{ item.middlename }} {{ item.lastname }}</template> -->
                <template #item.short_interests="{ item }">{{getActivitiesString(item.activities)}}</template>
                <template v-slot:items="users">
                  <!-- <td class="text-xs-right">{{ users.item.full_name }}</td> -->
                  <td class="text-xs-right">{{ users.item.firstname }}</td>
                  <td class="text-xs-right">{{ users.item.lastname }}</td>
                  <!-- <td class="text-xs-right">{{ users.item.userId }}</td> -->
                  <td class="text-xs-right">{{ users.item.nickname }}</td>
                  <td class="text-xs-right">{{  }}</td>
                </template>
              </v-data-table>

              <br><v-divider></v-divider><br>

              <v-card-actions>
                <v-spacer></v-spacer>
                <div>
                  <v-btn id="followBtn" @click="toggleFollowingActivity" class="mr-1" color="primary"> {{ following ? "Unfollow" : "Follow" }} </v-btn>
                </div>
                <div>
                  <v-btn :disabled="organiser" id="participateBtn" @click="toggleParticipation" class="mr-1" color="primary"> {{ participating ? "Unparticipate" : "Participate" }} </v-btn>
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

import {
  getActivity,
  followActivity,
  unfollowActivity,
  getIsFollowingActivity,
  getParticipants
} from '../controllers/activity.controller';
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '../scripts/Activity';
import * as authService from '../services/auth.service';
import * as activityController from '../controllers/activity.controller';
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "../scripts/User";
import {getActivityRole} from "@/models/activity.model";

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
      organiser: false,
      participating: false,
      following: false,
      confirmDeleteModal: false,
      startTimeString: '' as string,
      endTimeString: '' as string,
      headers: [
        // { text: 'Name', value: 'full_name' },
        { text: 'First Name', value: 'firstname' },
        { text: 'Last Name', value: 'lastname' },
        //{ text: 'User Id', value: 'profile_id'},
        { text: 'Nickname', value: 'nickname' },
        { text: 'Role', value: 'role' }
      ],
      noDataText: "No Participants",
      selectedUsers: [] as UserApiFormat[],
      users: [] as UserApiFormat[],
      errorMessage: "",
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
        getIsFollowingActivity(this.currentProfileId, this.activityId)
        .then((booleanResponse) => {
          this.following = booleanResponse;
        })
        activityController.getIsParticipating(this.currentProfileId, this.activityId)
        .then((booleanResponse) => {
          this.participating = booleanResponse;
        })
        activityController.getIsOrganising(this.currentProfileId, this.activityId)
        .then((booleanResponse) => {
          this.organiser = booleanResponse;
        })
      })
      .catch(() => {
        this.$router.back();
      });
    }

    this.search();
},

  computed: {

    selectedUser: function() {
      if (this.selectedUsers.length !== 1) {
        return null;
      }
      let selectedUser: UserApiFormat = this.selectedUsers[0];
      return selectedUser;
    },
    selectedUserIsAdmin: function() {
      if (this.selectedUsers.length !== 1) {
        return false;
      }

      let selectedUser: UserApiFormat = this.selectedUsers[0];
      return selectedUser.permission_level && selectedUser.permission_level >= 120;
    }
  },

  methods: {
    /**
     * Send the user to the activity's edit page
     */
    edit: function() {
      this.$router.push(
        `/profiles/${this.creatorId}/editActivity/${this.activityId}`
      );
    },
    /**
     * Toggle whether the user is following the activity
     */
    toggleFollowingActivity: function() {
      if (this.following) {
        unfollowActivity(this.currentProfileId, this.activityId)
        .then(() => {
          this.following = false
        })
        .catch((err) => {
          console.error(err)
        })
      } else {
        followActivity(this.currentProfileId, this.activityId)
        .then(() => {
          this.following = true
        })
        .catch((err) => {
          console.error(err)
        })
      }
    },
    /** Toggle the user's participation in this activity */
    toggleParticipation: function() {
      if (!this.participating) {
        this.organiser = false;
        activityController.participateInActivity(this.currentProfileId, this.activityId)
          .then(() => this.participating = true)
          .catch((e) => { console.error(e) });
      } else {
        activityController.removeActivityRole(this.currentProfileId, this.activityId)
          .then(() => this.participating = false)
          .catch((e) => { console.error(e) });
      }
    },
    /** Navigate back to the last page the user was on. */
    backButtonClicked() {
      this.$router.back();
    },
    /** Delete the activity */
    deleteActivity: async function() {
      this.confirmDeleteModal = false;
      activityController.deleteActivity(this.currentProfileId, this.activityId)
        .then(() => {
          this.$router.back();
        })
        .catch(err => {
          alert("An error occured while deleting the activity:\n" + err);
        });
    },
    /** Get a user-readable version of the start and end times
     *  Returns a string representing activity time frame
     */
    getDurationDescription: function(startTime: string, endTime: string): string {
      return activityController.describeDurationTimeFrame(startTime, endTime);
    },
    /**
     * Get whether activity has start and end times
     * Returns true if activity has start and end times
     */
    hasTimeFrame: function(activity: CreateActivityRequest): boolean {
      return activity.start_time != undefined && activity.end_time != undefined;
    },
    goToUser: function(userId: any) {
      this.$router.push("/profiles/" + userId.profile_id);
    },
    search: async function() {
      this.noDataText = "No participants found";
      this.errorMessage = "";
      try {
        let users = await getParticipants(this.activityId)
        let user;
        for (user of users) {
          if (this.creatorId == user.profile_id) {
            user.role = "CREATOR";
          } else {
            user.role = await getActivityRole(user.profile_id, this.activityId);
          }
        }
        this.users = users as UserApiFormat[];
      } catch (err) {
        if (err.response) {
          if (err.response.status == 400) {
            this.errorMessage = err.message;
          }
        } else if (err.message) {
          this.errorMessage = err.message;
        } else {
          this.errorMessage = "Unexpected error";
        }
        this.noDataText = this.errorMessage;
        this.users = [];
      }
    },
  }

});

export default Activity;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
