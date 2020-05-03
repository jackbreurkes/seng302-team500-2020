<template>
  <div id="CreateActivity">
    <v-form ref="createActivityForm">
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center">
          <v-col cols="12" sm="8" md="4">
            <v-card class="elevation-12" width="100%">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>CreateActivity:</v-toolbar-title>
              </v-toolbar>
              <v-card-text>
                <v-text-field
                  label="Activity Name"
                  ref="activityName"
                  id="activityName"
                  type="text"
                  v-model="createActivityRequest.activity_name"
                  :rules="inputRules.activityNameRules"
                ></v-text-field>

                <v-radio-group v-model="timeMode" row>
                <v-radio label="Continuous" value="continuous"></v-radio>
                <v-radio label="Duration" value="duration"></v-radio>
              </v-radio-group>

                <div v-if="timeMode === 'duration'">
                  <v-menu
                    ref="startDateMenu"
                    v-model="startDateMenu"
                    :close-on-content-click="false"
                    transition="scale-transition"
                    offset-y
                    max-width="290px"
                    min-width="290px"
                  >
                    <template v-slot:activator="{ on }">
                      <v-text-field
                        dense
                        filled
                        v-model="startDate"
                        v-on="on"
                        readonly
                        label="Start Date"
                        :rules="inputRules.startDateRules"
                      ></v-text-field>
                    </template>
                    <v-date-picker no-title v-model="startDate" @input="startDateMenu = false"></v-date-picker>
                  </v-menu>

                  <v-text-field
                    label="Start Time"
                    ref="startTime"
                    id="startTime"
                    type="time"
                    v-model="startTime"
                    :rules="inputRules.startTimeRules"
                  ></v-text-field>

                  <v-menu
                    ref="endDateMenu"
                    v-model="endDateMenu"
                    :close-on-content-click="false"
                    transition="scale-transition"
                    offset-y
                    max-width="290px"
                    min-width="290px"
                  >
                    <template v-slot:activator="{ on }">
                      <v-text-field
                        dense
                        filled
                        v-model="endDate"
                        v-on="on"
                        readonly
                        label="End Date"
                        :rules="inputRules.endDateRules"
                      ></v-text-field>
                    </template>
                    <v-date-picker no-title v-model="endDate" @input="endDateMenu = false"></v-date-picker>
                  </v-menu>

                  <v-text-field
                    label="End Time"
                    ref="endTime"
                    id="endTime"
                    type="time"
                    v-model="endTime"
                    :rules="inputRules.endTimeRules"
                  ></v-text-field>
                </div>

                <v-textarea
                  label="Describe your activity"
                  ref="description"
                  id="description"
                  v-model="createActivityRequest.description"
                  :rules="inputRules.descriptionRules"
                  outlined
                ></v-textarea>
                <v-text-field
                  label="Location"
                  ref="location"
                  id="location"
                  type="text"
                  v-model="createActivityRequest.location"
                  :rules="inputRules.locationRules"
                ></v-text-field>

                <div id="activity-type-chips" v-if="createActivityRequest.activity_type && createActivityRequest.activity_type.length > 0">
                  <v-chip
                    v-for="type in createActivityRequest.activity_type"
                    :key="type"
                    close
                    class="ma-2"
                    @click:close="removeActivityType(type)"
                  >{{ type }}</v-chip>
                </div>
    
                <v-autocomplete
                  :items="activityTypes"
                  color="white"
                  item-text="name"
                  label="Activity Types"  
                  v-model="selectedActivityType"
                  @input="addSelectedActivityType()"
                ></v-autocomplete>

              </v-card-text>
              <v-card-actions>
                <p class="pl-1" style="color: red">{{ errorMessage }}</p>
                <v-btn @click="cancelButtonClicked">Cancel</v-btn>
                <v-spacer />
                <v-btn @click="createButtonClicked" color="primary">Create</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-form>
  </div>
</template>


<script lang="ts">
import Vue from "vue";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '../scripts/Activity'
import * as activityController from '../controllers/activity.controller'
// app Vue instance
const CreateActivity = Vue.extend({
  name: "CreateActivity",

  // app initial state
  data: function() {
    return {
      createActivityRequest: {} as CreateActivityRequest,
      currentProfileId: NaN as number,
      timeMode: "",
      startDate: "",
      startTime: "",
      endDate: "",
      endTime: "",
      activityTypes: [] as string[],
      selectedActivityType: "" as string,
      startDateMenu: false,
      errorMessage: "",
      inputRules: {
        activityNameRules: [
          (v: string) => activityController.validateActivityName(v) || activityController.INVALID_ACTIVITY_NAME_MESSAGE
        ],
        startDateRules: [
          (v: string) => {
            return true || v; // TODO make error checking
          }
        ],
        endDateRules: [
          (v: string) => {
            return true || v; // TODO make error checking
          }
        ],
        startTimeRules: [
          (v: string) => {
            return true || v; // TODO make error checking
          }
        ],
        endTimeRules: [
          (v: string) => {
            return true || v; // TODO make error checking
          }
        ],
        descriptionRules: [
          (v: string) => activityController.validateDescription(v) || activityController.INVALID_DESCRIPTION_MESSAGE
        ],
        locationRules: [
          (v: string) => true || v //TODO location not implemented
        ],
        activityTypes: [
          (v: string) => true || v //TODO currently does not like syntax shown below but logic is there
          // (v: string) => activityController.validateActivityType(v, this.createActivityRequest) || activityController.INVALID_ACTIVITY_TYPE
        ],
      }
    };
  },

  created() {
    const profileId: number = parseInt(this.$route.params.profileId);
    this.currentProfileId = profileId;
    this.createActivityRequest = {}
    activityController.getAvailableActivityTypes()
      .then(activity => {
        this.activityTypes = activity})
      .catch(err => {console.error("unable to load activity types");
      console.error(err)});
  },

  methods: {

    addSelectedActivityType: async function() {
      if (!this.selectedActivityType) {
        return
      }
      await activityController.addActivityType(this.selectedActivityType, this.createActivityRequest);
      this.selectedActivityType = ""
    },

    removeActivityType: function(activityType: string) {
      activityController.removeActivityType(activityType, this.createActivityRequest);
    },

    cancelButtonClicked() {
      this.$router.push({ name: "profilePage" });
    },

    createButtonClicked() {
      activityController.createNewActivity(this.createActivityRequest, this.currentProfileId)
          .then(() => {
            history.go(0);
          })
          .catch(() => {
            alert(`${this.createActivityRequest.activity_type}`);
          });
    }
  }
});

export default CreateActivity;
</script>

<style>
[v-cloak] {
  display: none;
}

.mandatory {
  background-color: #ccf;
}

p {
  display: inline-block;
}
</style>
