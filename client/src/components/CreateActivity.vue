<template>
  <div id="CreateActivity">
    <v-form submit="false">
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
                  :rules="inputRules.activityName"
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

                <div id="passport-chips" v-if="createActivityRequest.activity_type && createActivityRequest.activity_type.length > 0">
                  <v-chip
                    v-for="activityType in createActivityRequest.activity_type"
                    :key="activityType"
                    close
                    class="ma-1"
                    @click:close="removeActivityType(activityType)"
                  >{{ passport }}</v-chip>
                </div>

                <v-autocomplete
                  :items="passportCountries"
                  color="white"
                  item-text="name"
                  label="Countries"
                  v-model="selectedCountry"
                  @input="addSelectedPassportCountry()"
                ></v-autocomplete>

              </v-card-text>
              <v-card-actions>
                <p class="pl-1" style="color: red">{{ errorMessage }}</p>
                <v-btn @click="cancelButtonClicked">Cancel</v-btn>
                <v-spacer />
                <v-btn @click="saveButtonClicked" color="primary">Create</v-btn>
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
import { removeActivityType, addActivityType } from '../controllers/activity.controller'

// app Vue instance
const CreateActivity = Vue.extend({
  name: "CreateActivity",
  el: "#CreateActivity",

  // app initial state
  data: function() {
    return {
      createActivityRequest: {} as CreateActivityRequest,
      activityName: "",
      timeMode: "",
      activityType: "",
      startDate: "",
      startTime: "",
      endDate: "",
      endTime: "",

      startDateMenu: false,
      errorMessage: "",
      inputRules: {
        activityNameRules: [
          (v: string) => true || v
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
          (v: string) => true || v
        ],
        locationRules: [
          (v: string) => true || v
        ],
        activityTypes: [
          (v: string) => true || v
        ],
      }
    };
  },

  created() {
    this.createActivityRequest = {}
  },

  methods: {

    addActivityType(activityType: string) {
      addActivityType(activityType, this.createActivityRequest);
    },

    removeActivityType(activityType: string) {
      removeActivityType(activityType, this.createActivityRequest);
    },

    cancelButtonClicked() {
      this.$router.push({ name: "profilePage" });
    },

    saveButtonClicked() {}
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
