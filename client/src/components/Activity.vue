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
                  <v-chip v-if="isHappeningNow()" class="mr-2" color="green">Happening Now</v-chip>
                  <v-chip v-if="isInPast()" class="mr-2" color="red">Activity Finished</v-chip>
                  <v-chip v-if="currentUsersProfileId === creatorId" outlined class="mr-1">Creator</v-chip>
                  <v-chip v-if="organiser" outlined class="mr-1">Organiser</v-chip>
                  <v-chip v-if="following" outlined class="mr-1">Following</v-chip>
                  <v-chip v-if="participating" outlined class="mr-1">Participating</v-chip>
                  <v-menu v-if="organiser || currentlyHasAuthority" bottom left offset-y id="editBurger">
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
                      <v-list-item v-if="currentlyHasAuthority" @click="confirmDeleteModal = true">
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
                <v-chip
                    class="ma-2"
                    color="blue"
                    outlined
                    label>
                    Followers: {{followers}}
                </v-chip>
                <v-chip
                        class="ma-2"
                        color="blue"
                        outlined
                        label>
                    Participants: {{participants}}
                </v-chip>
                <br>
              <v-container>
                <v-row>
              <h3>Activity Information</h3>
                </v-row>
              <v-row>
                <v-col cols="2"><v-icon class="mr-2">mdi-text</v-icon>Description:</v-col>
                <v-col>{{activity.description}}</v-col>
              </v-row>
              <v-row>
                <v-col cols="2"><v-icon class="mr-2">mdi-map-marker</v-icon> Location:</v-col>
                <v-col cols="6">{{activity.location}}</v-col>
                <v-col><v-btn v-if="hasPin" v-on:click="viewOnMap" text small color="primary"><v-icon>mdi-map-marker</v-icon> Show on map </v-btn> </v-col>
                </v-row>
              <v-row v-if="hasTimeFrame(activity)">
                <v-col cols="2"><v-icon class="mr-2">mdi-clock-outline</v-icon> Timeframe:</v-col>
                <v-col md="auto">
                  Starts:<br>
                  Ends:
                </v-col>
                <v-col>
                  {{formatDate(activity.start_time)}}
                  <br>
                  {{formatDate(activity.end_time)}}
                </v-col>
              </v-row>
              <v-row v-else>
                <v-col cols="4">Timeframe:</v-col>
                <v-col cols="8">Activity is continuous</v-col>
              </v-row>
                <v-chip
                class="mr-2 mb-2"
                v-for="activityType of activity.activity_type"
                v-bind:key="activityType"
                outlined
              >{{ activityType }}</v-chip> 
              <br>
              </v-container>

              <v-divider></v-divider><br>

              <v-data-table
                  :no-data-text="noDataText"
                  :headers="headers"
                  :items="users"
                  item-key="profile_id"
                  @click:row="goToUser"
                  single-select
                  v-model="selectedUsers"
                  sort-by="role"
              >
                <template #full_name="{ item }">{{ item.firstname }} {{ item.middlename }} {{ item.lastname }} {{ item.role }}</template>
                <template v-slot:items="users">
                  <td class="text-xs-right">{{ users.item.firstname }}</td>
                  <td class="text-xs-right">{{ users.item.lastname }}</td>
                  <td class="text-xs-right">{{ users.item.nickname }}</td>
                  <td class="text-xs-right">{{ users.item.role }}</td>
                </template>
              </v-data-table>

              <br><v-divider></v-divider><br>

              <v-expansion-panels flat style="border: 1px solid silver;">
                  <v-expansion-panel>
                    <v-expansion-panel-header>Activity Outcomes</v-expansion-panel-header>
                    <v-expansion-panel-content>
                      <v-sheet :v-model="updated">
                        <p v-if="activity.outcomes == undefined || Object.keys(activity.outcomes).length == 0">
                          Activity has no associated outcomes
                        </p>
                        <div v-if="Object.keys(currentResults) != undefined && Object.keys(currentResults).length > 0">
                        Current results:
                          <v-card
                            class="pa-2"
                            v-for="(result, index) in currentResults"
                            v-bind:item="result"
                            v-bind:index="index"
                            :key="index"
                            outlined
                            >
                            <v-row align="end">
                              <v-col sm="12" md="6">
                                Outcome: {{result.description}}
                              </v-col>
                              <v-col sm="12" md="6">
                                <label>Result: {{result.units == undefined ? result.score : result.score + " " + result.units}}</label>
                              </v-col>
                            </v-row>
                            <v-row>
                              <v-col xs="12" md="6">
                                Completion date: {{result.date}}
                              </v-col>
                              <v-col xs="12" md="6">
                                Completion time: {{result.time.split(':')[0]+':'+result.time.split(':')[1]}}
                              </v-col>
                            </v-row>
                            <v-row justify="end">
                              <v-spacer></v-spacer>
                              <div class="mr-3">
                                <v-btn @click="outcomeIdToRemove = index, removeResultModal = true" right color="error">
                                  Remove
                                </v-btn>
                              </div>
                            </v-row>
                        </v-card>
                        </div>
                        <br>
                        <div v-if="Object.keys(currentResults) == undefined || (activity.outcomes!=undefined && activity.outcomes.length - Object.keys(currentResults).length > 0)">
                        Add new results:
                        <v-card
                            class="pa-2 mb-2"
                            v-for="(item, index) in activity.outcomes.filter(outcome => !(outcome.outcome_id in currentResults))"
                            v-bind:item="item"
                            v-bind:index="index"
                            :key="index"
                            outlined
                            v-model="updated"
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
                                <v-btn @click="saveParticipantOutcomeClicked(item.outcome_id)" right color="primary">
                                  Save
                                </v-btn>
                              </div>
                            </v-row>
                        </v-card>
                        </div>
                      </v-sheet>
                    </v-expansion-panel-content>
                  </v-expansion-panel>
                </v-expansion-panels>

                <!-- save outcome outside time frame confirmation modal -->
                <v-dialog v-model="displayConfirmTimeframeModal" width="350">

                      <v-card>
                        <v-card-title class="headline" primary-title>Confirm Your Result Time</v-card-title>

                        <v-card-text>The completion time you are trying to enter for this result is not within the activity's suggested duration.
                          Are you sure you wish to continue?</v-card-text>

                        <v-divider></v-divider>

                        <v-card-actions>
                          <v-btn text @click="displayConfirmTimeframeModal = false">Cancel</v-btn>
                          <v-spacer></v-spacer>
                          <v-btn color="success" text @click="saveParticipantOutcome(selectedOutcomeId)">Confirm</v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-dialog>

              <v-card-actions>
                <v-spacer></v-spacer>
                <div>
                  <v-btn id="followBtn" @click="toggleFollowingActivity" class="mr-1" color="primary" :outlined="following" width="150"> {{ following ? "Unfollow" : "Follow" }} </v-btn>
                </div>
                <div>
                  <v-btn :disabled="organiser" id="participateBtn" @click="toggleParticipation" class="mr-1" :outlined="participating" color="primary" width="150"> {{ participating ? "Unparticipate" : "Participate" }} </v-btn>
                </div>
              </v-card-actions>

              </v-card-text>
            <v-row>
            </v-row>

          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <v-dialog v-model="removeResultModal" width="290">
      <v-card>
        <v-card-title class="headline" primary-title>Remove result?</v-card-title>
        <v-card-text>This operation cannot be undone.</v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-btn text @click="removeResultModal = false">Cancel</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="error" text @click="removeResult">Remove</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
import { CreateActivityRequest, ActivityOutcomes, ParticipantResult, ParticipantResultDisplay } from '../scripts/Activity';
import * as authService from '../services/auth.service';
import * as activityController from '../controllers/activity.controller';
import FormValidator from "../scripts/FormValidator";
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "../scripts/User";
// eslint-disable-next-line no-unused-vars
import { UserRoleFormat } from '@/scripts/UserRoleFormat';

// app Vue instance
const Activity = Vue.extend({
  name: "Activity",

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
    return {
      hasPin: false as boolean,
      currentUsersProfileId: NaN as number,
      activityId: NaN as number,
      creatorId: NaN as number,
      activity: [] as CreateActivityRequest,
      organiser: false,
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
      selectedOutcomeId: NaN as number,
      currentResults: {} as Record<number, ParticipantResultDisplay>,
      removeResultModal: false,
      outcomeIdToRemove: NaN as number,
      updated: false,
      possibleOutcomes: {} as Record<number, ActivityOutcomes>,
      headers: [
        { text: 'First Name', value: 'firstname' },
        { text: 'Last Name', value: 'lastname' },
        { text: 'Nickname', value: 'nickname' },
        { text: 'Role', value: 'role' }
      ],
      noDataText: "No Participants",
      selectedUsers: [] as UserApiFormat[],
      users: [] as UserApiFormat[],
      errorMessage: "",
      followers: NaN as number,
      participants: NaN as number,
      displayConfirmTimeframeModal: false,
      currentlyHasAuthority: false as Boolean
    };
  },

  created() {
    let myProfileId = authService.getMyUserId();
    if (myProfileId == null) {
      this.$router.push('login');
    } else {
      this.currentUsersProfileId = myProfileId;
    }

    const activityId: number = parseInt(this.$route.params.activityId);
    const creatorId: number = parseInt(this.$route.params.profileId);
    
    this.displayActivity(activityId, creatorId);

    this.currentlyHasAuthority = myProfileId == creatorId || authService.isAdmin();
  },

  methods: {
    /**
     * Displays the activity (populates the page)
     */
    displayActivity: function(activityId: number, creatorId: number) {
      if (isNaN(creatorId) || isNaN(activityId)) {
        this.$router.back();
        return;
      }
      this.activityId = activityId;
      this.creatorId = creatorId;

      getActivity(creatorId, activityId)
      .then((res) => {
        this.activity = res;
        if(this.activity.geoposition !== undefined) {
          this.hasPin = true;
        }
        if (this.activity.num_followers != null) {
            this.followers = this.activity.num_followers;
        }
        if (this.activity.num_participants != null) {
            this.participants = this.activity.num_participants;
        }
        getIsFollowingActivity(this.currentUsersProfileId, this.activityId)
        .then((isFollowing) => {
          this.following = isFollowing;
        })
        activityController.getIsParticipating(this.currentUsersProfileId, this.activityId)
        .then((isParticipating) => {
          this.participating = isParticipating;
        })
        if (this.creatorId == this.currentUsersProfileId) {
          this.organiser = true;
        } else {
          activityController.getIsOrganising(this.currentUsersProfileId, this.activityId)
          .then((booleanResponse) => {
            this.organiser = booleanResponse;
          })
        }

        let outcome_array = this.activity.outcomes as ActivityOutcomes[];
        for (let outcome_index in outcome_array) {
          if (this.activity.outcomes === undefined) continue;
          let outcome_id = this.activity.outcomes[outcome_index].outcome_id;
          if (outcome_id === undefined) continue;
          this.participantOutcome[outcome_id] = {"score": ""} as ParticipantResult;
          this.possibleOutcomes[outcome_id] = this.activity.outcomes[outcome_index] as ActivityOutcomes;
        }
      })
      .then(() => {
        activityController.getParticipantResults(this.currentUsersProfileId, this.activityId)
        .then((results) => {
          let participantResults = {} as Record<number, ParticipantResultDisplay>;
          for (let index in results) {
            let date = results[index].completed_date.split("T")[0];
            let time = results[index].completed_date.split("T")[1];
            participantResults[results[index].outcome_id] = {
                'score': results[index].result,
                'date': date,
                'time': time,
                'description': this.possibleOutcomes[results[index].outcome_id].description,
                'units': this.possibleOutcomes[results[index].outcome_id].units
            } as ParticipantResultDisplay;
          }
          this.currentResults = participantResults;
        });
      })
      .catch(() => {
        this.$router.back();
      });

      // Populates the datatable that holds all the participants/oraganisers
      this.search();
    },

    isHappeningNow: function(){
      if(this.activity.end_time !== undefined && this.activity.start_time !== undefined){
        if(activityController.isFutureDateTime(this.activity.end_time) && !activityController.isFutureDateTime(this.activity.start_time)){
          return true;
        }
      }
      return false;
    },

    isInPast: function() {
      if(this.activity.end_time !== undefined){
        if(!activityController.isFutureDateTime(this.activity.end_time)){
          return true;
        }
      }
      return false;
    },

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
        this.followers = this.followers - 1;
        unfollowActivity(this.currentUsersProfileId, this.activityId)
        .then(() => {
          this.following = false
        })
        .catch((err) => {
          console.error(err)
        })
        .then(() => {this.$root.$emit('refreshPins')});
      } else {
          this.followers = this.followers + 1;
          followActivity(this.currentUsersProfileId, this.activityId)
        .then(() => {
          this.following = true
        })
        .catch((err) => {
          console.error(err)
        })
        .then(() => {this.$root.$emit('refreshPins')});
      }
    },
    /** Toggle the user's participation in this activity */
    toggleParticipation: async function() {
      if (!this.participating) {
        this.organiser = false;
        await activityController.participateInActivity(this.currentUsersProfileId, this.activityId);
        this.participating = true;
        this.participants = this.participants + 1;
      } else {
        await activityController.removeActivityRole(this.currentUsersProfileId, this.activityId)
        this.participating = false;
        this.participants = this.participants - 1;
      }
      this.$root.$emit('refreshPins')
      
      // Reload the datatable when you participate/unparticipate
      this.users = [];
      this.search();
    },
    /** Navigate back to the last page the user was on. */
    backButtonClicked() {
      this.$router.back();
    },
    /** Delete the activity */
    deleteActivity: async function() {
      this.confirmDeleteModal = false;
      activityController.deleteActivity(this.currentUsersProfileId, this.activityId)
        .then(() => {
          this.$router.back();
          this.$root.$emit('refreshPins');
        })
        .catch(err => {
          alert("An error occured while deleting the activity:\n" + err);
        });
    },
    /** Get a user-readable version of a timestamp
     *  Returns a string representing activity time frame
     */
    formatDate: function(time: string): string {
      return activityController.describeDate(time);
    },
    /**
     * Get whether activity has start and end times
     * Returns true if activity has start and end times
     */
    hasTimeFrame: function(activity: CreateActivityRequest): boolean {
      return activity.start_time != undefined && activity.end_time != undefined;
    },

    /**
     * Attempt to add a new participant result for the specified outcome on this activity.
     * displays a modal if the time frame is outside the expected time frame.
     */
    saveParticipantOutcomeClicked: async function(outcomeId: number) {
      let result = this.participantOutcome[outcomeId].score;
      let completedDate = this.participantOutcome[outcomeId].date;
      let completedTime = this.participantOutcome[outcomeId].time;
      this.selectedOutcomeId = outcomeId;

      if (completedDate === undefined) {
        alert("You must select a date");
        return;
      }
      if (completedTime === undefined) {
        alert("You must select a time");
        return;
      }
      if (result === undefined || result === "") {
        alert("You must enter a result");
        return;
      }
      let completedTimestamp = activityController.getApiDateTimeString(completedDate, completedTime);
      if (!this.hasTimeFrame(this.activity) || activityController.timeIsWithinRange(this.activity.start_time!, this.activity.end_time!, completedTimestamp)) {
        await this.saveParticipantOutcome(outcomeId);
      } else {
        this.displayConfirmTimeframeModal = true;
      }
    },

    /**
     * Add a new participant result for the specified outcome on this activity.
     */
    async saveParticipantOutcome(outcomeId: number) {
      let result = this.participantOutcome[outcomeId].score;
      let completedDate = this.participantOutcome[outcomeId].date;
      let completedTime = this.participantOutcome[outcomeId].time;
      let completedTimestamp = activityController.getApiDateTimeString(completedDate, completedTime);
      this.displayConfirmTimeframeModal = false;
      if (this.currentUsersProfileId !== this.creatorId && this.participating === false && this.organiser === false) {
        await this.toggleParticipation();
      }

      activityController.createParticipantResult(this.activityId, outcomeId, result, completedTimestamp)
        .then(() => {
          this.currentResults[outcomeId] = this.participantOutcome[outcomeId];
          if (this.activity.outcomes === undefined) {
            this.currentResults[outcomeId].description = "Description not found";
            this.currentResults[outcomeId].units = "";
          } else {
            this.currentResults[outcomeId].description = this.possibleOutcomes[outcomeId].description;
            this.currentResults[outcomeId].units = this.possibleOutcomes[outcomeId].units;
          }
          this.updated = !this.updated; // Force component showing outcomes to refresh
        })
        .catch((err) => {alert(err.message)});
    },

    /** Remove the selected result from the activity */
    removeResult: function() {
      this.removeResultModal = false;
      activityController.removeParticipantResult(this.activityId, this.outcomeIdToRemove)
      .then(() => {
        delete this.currentResults[this.outcomeIdToRemove];
        this.participantOutcome[this.outcomeIdToRemove].score = "";
        delete this.participantOutcome[this.outcomeIdToRemove].date;
        delete this.participantOutcome[this.outcomeIdToRemove].time;
        this.updated = !this.updated; // Force component showing outcomes to refresh
      })
    },

    /** Routes to the user you click on, on the datatable **/
    goToUser: function(userId: any) {
      this.$router.push("/profiles/" + userId.profile_id);
    },

    /** Populates the datatable containing all the participants/organisers **/
    search: async function() {
      this.noDataText = "No participants found";
      this.errorMessage = "";
      try {
        let users = await getParticipants(this.activityId)
        this.users = users as UserRoleFormat[];
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

    /** Place a pin for this activity on the map */
    viewOnMap: async function() {
      this.$root.$emit('showActivityOnMap', this.activity);
    }
  },

  watch: {
    $route(to) {
      const activityId: number = parseInt(to.params.activityId);
      const creatorId: number = parseInt(to.params.profileId);
      this.displayActivity(activityId, creatorId);
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
