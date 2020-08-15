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
                  <v-menu bottom left offset-y>
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
                      <v-list-item @click="confirmDeleteModal = true">
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

              <p> Please insert a UserRoleList component here! </p>

              <br><v-divider></v-divider><br>

              <v-expansion-panels flat style="border: 1px solid silver;">
                  <v-expansion-panel>
                    <v-expansion-panel-header>Activity Outcomes</v-expansion-panel-header>
                    <v-expansion-panel-content>
                      <v-sheet>
                          <v-card
                            class="pa-2"
                            :v-if="activity.outcomes.length > 0"
                            v-for="(item, index) in activity.outcomes"
                            v-bind:item="item"
                            v-bind:index="index"
                            :key="index"
                            outlined
                            >
                            <v-row align="end">
                              <v-col sm="12" md="6">
                                {{item.description}}:
                              </v-col>
                              <v-col sm="8" md="4">
                                <v-text-field
                                  label="Your result"
                                  type="text"
                                  v-model="participantOutcome[item.outcome_id]['score']"
                                  :rules="inputRules.resultRules"
                                  hide-details
                                ></v-text-field>
                              </v-col>
                              <v-col sm="4" md="2">
                                {{item.units}}
                              </v-col>
                            </v-row>
                            <v-row>
                              <v-col xs="12" md="6">
                                <v-text-field label="Completion date" type="date" v-model="participantOutcome[item.outcome_id]['date']"></v-text-field>
                              </v-col>
                              <v-col xs="12" md="6">
                                <v-text-field label="Completion time" type="time" v-model="participantOutcome[item.outcome_id]['time']"></v-text-field>
                              </v-col>
                            </v-row>
                            <v-row justify="end">
                              <v-spacer></v-spacer>
                              <div class="mr-3">
                                <v-btn @click="saveParticipantOutcome(item.outcome_id)" right color="primary">
                                  Save
                                </v-btn>
                              </div>
                            </v-row>
                        </v-card>
                      </v-sheet>
                    </v-expansion-panel-content>
                  </v-expansion-panel>
                </v-expansion-panels>

              <v-card-actions>
                <v-spacer></v-spacer>
                <div>
                  <v-btn id="followBtn" @click="toggleFollowingActivity" class="mr-1" color="primary"> {{ following ? "Unfollow" : "Follow" }} </v-btn>
                </div>
                <div>
                  <v-btn id="participateBtn" @click="toggleParticipation" class="mr-1" color="primary"> {{ participating ? "Unparticipate" : "Participate" }} </v-btn>
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
import { CreateActivityRequest, ActivityOutcomes, ParticipantResult } from '../scripts/Activity';
import * as authService from '../services/auth.service';
import * as activityController from '../controllers/activity.controller';
import FormValidator from "../scripts/FormValidator";

// app Vue instance
const Activity = Vue.extend({
  name: "Activity",

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
    return {
      currentProfileId: NaN as number,
      activityId: NaN as number,
      creatorId: NaN as number,
      activity: [] as CreateActivityRequest,
      participating: false,
      following: false,
      confirmDeleteModal: false,
      startTimeString: '' as string,
      endTimeString: '' as string,
      participantOutcome: {} as Record<number, ParticipantResult>,
      inputRules: {
        resultRules: [
          (v: string) =>
            formValidator.checkResultValidity(v)
        ]
      },
      currentResults: {} as Record<number, ParticipantResult>
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

      activityController.getParticipantResults(this.currentProfileId, this.activityId)
      .then((results) => {
        for (let index in results) {
          let date = results[index].completed_date.split(" ")[0];
          let time = results[index].completed_date.split(" ")[1];
          this.currentResults[results[index].outcome_id] = {
              'score': results[index].result,
              'date': date,
              'time': time
          } as ParticipantResult;
        }
      });

      getActivity(creatorId, activityId)
      .then((res) => {
        this.activity = res;
        getIsFollowingActivity(this.currentProfileId, this.activityId)
        .then((following) => {
          this.following = following;
        })
        activityController.getIsParticipating(this.currentProfileId, this.activityId)
        .then((participating) => {
          this.participating = participating
        })

        let outcome_array = this.activity.outcomes as ActivityOutcomes[];
        for (let outcome_index in outcome_array) {
          if (this.activity.outcomes === undefined) continue;
          let outcome_id = this.activity.outcomes[outcome_index].outcome_id;
          if (outcome_id === undefined) continue;
          this.participantOutcome[outcome_id] = {"score": ""} as ParticipantResult;
        }
      })
      .catch(() => {
        this.$router.back();
      });
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

    saveParticipantOutcome: async function(outcomeId: number) {
      let result = this.participantOutcome[outcomeId].score;
      let completedDate = this.participantOutcome[outcomeId].date;
      let completedTime = this.participantOutcome[outcomeId].time;
      let completedTimestamp = activityController.getApiDateTimeString(completedDate, completedTime);
      
      try {
        if (completedDate === undefined) {
          throw new Error("You must select a date");
        }
        await activityController.createParticipantResult(this.activityId, outcomeId, result, completedTimestamp);
      } catch (err) {
        alert(err.message);
      }
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
