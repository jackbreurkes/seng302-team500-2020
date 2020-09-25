<template>
  <div id="CreateActivity">
    <v-form ref="createActivityForm">
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center">
          <v-col xs="12" sm="10" md="6" lg="4">
            <v-card class="elevation-12" width="100%">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>
                  <v-btn dark icon @click="cancelButtonClicked">
                    <v-icon>mdi-arrow-left</v-icon>
                  </v-btn>
                  {{this.isEditing ? "Edit Activity" : "Create Activity"}}
                </v-toolbar-title>
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
                <v-sheet style="border: 1px solid silver;" class="pa-2 mb-4">
                  <v-switch
                    v-model="createActivityRequest.continuous"
                    label="Does this activity have a set start and end date?"
                    :true-value="false"
                    :false-value="true"
                  ></v-switch>
                  <v-expand-transition>
                    <div
                      v-if="!createActivityRequest.continuous & createActivityRequest.continuous !== undefined"
                    >
                      <v-row align="center" justify="center">
                        <v-col cols="6" sm="12" md="6">
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
                                v-model="startDate"
                                v-on="on"
                                readonly
                                label="Start Date"
                                :rules="inputRules.startDateRules"
                              ></v-text-field>
                            </template>
                            <v-date-picker
                              no-title
                              v-model="startDate"
                              @input="startDateMenu = false"
                            ></v-date-picker>
                          </v-menu>
                        </v-col>
                        <v-col cols="6" sm="12" md="6">
                          <v-text-field
                            label="Start Time"
                            ref="startTime"
                            id="startTime"
                            type="time"
                            v-model="startTime"
                            :rules="inputRules.startTimeRules"
                          ></v-text-field>
                        </v-col>
                      </v-row>
                      <v-row align="center" justify="center">
                        <v-col cols="6" sm="12" md="6">
                          <v-menu
                            ref="endDateMenu"
                            :close-on-content-click="false"
                            transition="scale-transition"
                            offset-y
                            max-width="290px"
                            min-width="290px"
                          >
                            <template v-slot:activator="{ on }">
                              <v-text-field
                                v-model="endDate"
                                v-on="on"
                                readonly
                                label="End Date"
                                :rules="inputRules.endDateRules"
                              ></v-text-field>
                            </template>

                            <v-date-picker no-title v-model="endDate" @input="endDateMenu = false"></v-date-picker>
                          </v-menu>
                        </v-col>
                        <v-col cols="6" sm="12" md="6">
                          <v-text-field
                            label="End Time"
                            ref="endTime"
                            id="endTime"
                            type="time"
                            v-model="endTime"
                            :rules="inputRules.endTimeRules"
                          ></v-text-field>
                        </v-col>
                      </v-row>
                    </div>
                  </v-expand-transition>
                </v-sheet>

                <v-textarea
                  label="Describe your activity"
                  ref="description"
                  id="description"
                  v-model="createActivityRequest.description"
                  :rules="inputRules.descriptionRules"
                  outlined
                ></v-textarea>
                <v-row align="baseline">
                  <v-col>
                    <v-text-field
                      label="Location"
                      ref="location"
                      id="location"
                      type="text"
                      v-model="createActivityRequest.location"
                      :rules="inputRules.locationRules"
                      @keyup.enter.native="viewOnMap"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="3">
                    <v-btn v-on:click="viewOnMap" text small color="primary"><v-icon>mdi-map-marker</v-icon> Map</v-btn>
                  </v-col>
                </v-row>
              
                <v-autocomplete
                  :items="activityTypes"
                  color="white"
                  item-text="name"
                  label="Activity Types"
                  id="activityTypes"
                  placeholder="Add Activity Types"
                  v-model="createActivityRequest.activity_type"
                  chips
                  deletable-chips
                  multiple
                ></v-autocomplete>
                <v-expansion-panels flat style="border: 1px solid silver;">
                  <v-expansion-panel>
                    <v-expansion-panel-header>
                      <v-container>
                    <v-row>Activity Outcomes</v-row>
                    <br>
                    <v-divider> </v-divider>
                    <br>
                    <v-row>
                      Let participants record their own results against challenges/milestones that you create
                     </v-row>
                      </v-container>
                    </v-expansion-panel-header>
                    
                    <v-expansion-panel-content> 
                      <v-sheet>
                        <v-row
                          align="start"
                          :v-if="createActivityRequest.outcomes.length > 0"
                          v-for="(item, index) in createActivityRequest.outcomes"
                          v-bind:item="item"
                          v-bind:index="index"
                          :key="index"
                        >
                          <v-col xs="11" md="5">
                            <v-text-field
                              hide-details
                              v-model="item.description"
                              dense
                              placeholder="e.g. Time to run 4km"
                              label="Description"
                              id="outcomeDescription"
                              outlined
                              :rules="[value => !!value]"
                            ></v-text-field>
                          </v-col>
                          <v-col xs="11" md="5">
                            <v-text-field
                              hide-details
                              v-model="item.units"
                              dense
                              placeholder="e.g. minutes"
                              label="Units"
                              id="outcomeUnits"
                              outlined
                              :rules="[value => !!value]"
                            ></v-text-field>
                          </v-col>
                          <v-col cols="2">
                            <v-btn
                              icon
                              large
                              color="#ff6666"
                              @click="removeActivityOutcomeRow(index)"
                            >
                              <v-icon>mdi-close-circle-outline</v-icon>
                            </v-btn>
                          </v-col>
                        </v-row>
                        <v-row align="start">
                          <v-spacer />
                          <v-spacer />
                          <v-col cols="2">
                            <v-btn icon large color="primary" @click="addNewOutcome()">
                              <v-icon>mdi-plus-circle-outline</v-icon>
                            </v-btn>
                          </v-col>
                        </v-row>
                      </v-sheet>
                    </v-expansion-panel-content>
                  </v-expansion-panel>
                </v-expansion-panels>

                <v-expansion-panels v-if="isEditing" flat class="mt-3" style="border: 1px solid silver;">
                  <v-expansion-panel>
                    <v-expansion-panel-header>Activity Organisers</v-expansion-panel-header>
                    <v-expansion-panel-content>
                      <v-sheet>
                        <div v-for="organiser in organisers" :key="organiser">
                          <v-chip
                            class="ml-1 mb-2"
                            close
                            @click:close="deleteOrganiser(organiser)"
                          >{{ organiser.primary_email }}</v-chip>
                        </div>
                        <v-row class="d-flex align-center ml-1">
                          <v-text-field
                            v-if="this.isEditing"
                            label="Enter organiser Email"
                            ref="organiserEmail"
                            id="organiserEmail"
                            type="email"
                            v-model="organiserEmail"
                          ></v-text-field>
                          <v-btn @click="addOrganiser" class="ml-3 mr-5">Add</v-btn>
                        </v-row>
                      </v-sheet>
                    </v-expansion-panel-content>
                  </v-expansion-panel>
                </v-expansion-panels>

                <p class="pl-1" style="color: red">{{ errorMessage }}</p>

                <v-row justify="end">
                  <v-btn @click="cancelButtonClicked" class="ma-1" width="100" outlined>Cancel</v-btn>

                  <div v-if="this.isEditing && this.isCreator">
                    <v-dialog v-model="confirmDeleteModal" width="290">
                      <template v-slot:activator="{ on }">
                        <v-btn v-on="on" color="error" class="ma-1" width="100" outlined>Delete</v-btn>
                      </template>

                      <v-card>
                        <v-card-title class="headline" primary-title>Delete activity?</v-card-title>

                        <v-card-text>This operation cannot be undone.</v-card-text>

                        <v-divider></v-divider>

                        <v-card-actions>
                          <v-btn text @click="confirmDeleteModal = false">Cancel</v-btn>
                          <v-spacer></v-spacer>
                          <v-btn color="error" text @click="deleteButtonClicked">Delete</v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-dialog>
                  </div>

                  <v-btn
                    @click="createButtonClicked"
                    id="createButton"
                    color="primary"
                    class="ma-1 mr-3"
                    width="100"
                    :loading="isSubmitting"
                  >{{this.isEditing ? "Save" : "Create"}}</v-btn>
                </v-row>
              </v-card-text>
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
import { CreateActivityRequest, ActivityOutcomes } from "../scripts/Activity";
import * as activityController from "../controllers/activity.controller";
import * as activityModel from "../models/activity.model";
import * as userSearch from "../controllers/userSearch.controller";
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "../scripts/User";
// eslint-disable-next-line no-unused-vars
import { LocationCoordinatesInterface } from '../scripts/LocationCoordinatesInterface';
import { getMyUserId } from "../services/auth.service"

// app Vue instance
const CreateActivity = Vue.extend({
  name: "CreateActivity",

  // app initial state
  data: function() {
    return {
      isActivityOutcomesExpanded: false,
      isSubmitting: false as boolean,
      createActivityRequest: {
        continuous: true
      } as CreateActivityRequest,
      organiserEmail: "",
      organisers: [] as UserApiFormat[],
      isEditing: false as boolean,
      isCreator: true as boolean,
      editingId: NaN as number,
      currentProfileId: NaN as number,
      startDate: "",
      startTime: "",
      endDate: "",
      endTime: "",
      organiserId: NaN as number,
      activityTypes: [] as string[],
      selectedActivityType: "" as string,
      startDateMenu: false,
      confirmDeleteModal: false,
      errorMessage: "",
      inputRules: {
        activityNameRules: [
          (v: string) =>
            activityController.validateActivityName(v) ||
            activityController.INVALID_ACTIVITY_NAME_MESSAGE
        ],
        startDateRules: [
          (v: string) => {
            return (
              activityController.isFutureDate(v) ||
              activityController.INVALID_DATE_MESSAGE
            );
          }
        ],
        startTimeRules: [
          (v: string) => {
            return (
              activityController.isValidTime(v) ||
              activityController.INVALID_TIME_MESSAGE
            );
          }
        ],
        endTimeRules: [
          (v: string) => {
            return (
              activityController.isValidTime(v) ||
              activityController.INVALID_TIME_MESSAGE
            );
          }
        ],
        descriptionRules: [
          (v: string) =>
            activityController.validateDescription(v) ||
            activityController.INVALID_DESCRIPTION_MESSAGE
        ],
        locationRules: [
          (v: string) => true || v //TODO location not implemented
        ],
        activityTypes: [
          (v: string) => true || v //TODO currently does not like syntax shown below but logic is there
          // (v: string) => activityController.validateActivityType(v, this.createActivityRequest) || activityController.INVALID_ACTIVITY_TYPE
        ]
      }
    };
  },

  created() {
    const profileId: number = parseInt(this.$route.params.profileId);
    this.currentProfileId = profileId;
    activityController
      .getAvailableActivityTypes()
      .then(availableActivityTypes => {
        this.activityTypes = availableActivityTypes;
      })
      .catch(err => {
        console.error("unable to load activity types");
        console.error(err);
      });

    if (this.$route.params.activityId) {
      this.editingId = parseInt(this.$route.params.activityId);
      this.isEditing = true;
      this.populateFields(this.editingId);
      if (
        !this.createActivityRequest.continuous &&
        this.createActivityRequest.start_time !== undefined &&
        this.createActivityRequest.end_time !== undefined
      ) {
        this.startDate = activityController.getDateFromISO(
          this.createActivityRequest.start_time
        );
        this.endDate = activityController.getDateFromISO(
          this.createActivityRequest.end_time
        );
      }
      activityController
        .getActivityOrganisers(this.editingId)
        .then(organisers => {
          this.organisers = organisers;
          //checking if each organiser is the creaotr
          this.organisers.forEach(this.checkIsCreator);
        })
        .catch(() => {});
    }

    this.checkOutcomesLength();
  },

  methods: {
    /**
     * tries to add the currently entered organiser to the set of organisers for this activity.
     */
    async addOrganiser() {
      for (let organiser of this.organisers) {
        if (organiser.primary_email === this.organiserEmail) {
          this.errorMessage = "this organiser has already been added";
          return;
        }
      }

      try {
        if (this.isEditing) {
          let users = await userSearch.searchUsers({
            email: this.organiserEmail,
            exactEmail: "true"
          });
          activityModel.setActivityRole(
            users[0].profile_id,
            this.editingId,
            "Organiser"
          );
          this.organisers.push(users[0]);
          this.errorMessage = "";
          this.organiserEmail = "";
        }
      } catch (err) {
        this.errorMessage =
          "Could not find acceptable profile with that e-mail";
        return;
      }
    },
    checkIsCreator: function(user: UserApiFormat){
      if(user.profile_id === getMyUserId()){
        this.isCreator = false;
      }
    },
    /**
     * removes an organiser from the activity.
     * @param organiserToDelete the organiser to remove from the activity
     */
    async deleteOrganiser(organiserToDelete: UserApiFormat) {
      if (!organiserToDelete.profile_id) {
        return;
      }
      activityController
        .removeActivityRole(organiserToDelete.profile_id, this.editingId)
        .then(() => {
          this.organisers = this.organisers.filter(
            org => org.profile_id !== organiserToDelete.profile_id
          );
        })
        .catch(err => {
          this.errorMessage = err.message;
        });
    },

    checkOutcomesLength: function() {
      if (
        this.createActivityRequest.outcomes === undefined ||
        this.createActivityRequest.outcomes.length == 0
      ) {
        this.createActivityRequest.outcomes = [
          { description: "", units: "" } as ActivityOutcomes
        ];
        this.$forceUpdate();
      }
    },
    addNewOutcome: function() {
      if (this.createActivityRequest.outcomes !== undefined) {
        for (let index in this.createActivityRequest
          .outcomes as ActivityOutcomes[]) {
          let item = this.createActivityRequest.outcomes[index];
          if (item.description.length == 0 || item.units.length == 0) {
            alert("More than one empty outcome is not allowed");
            return;
          }
        }
        this.createActivityRequest.outcomes.push({
          description: "",
          units: ""
        } as ActivityOutcomes);
        this.$forceUpdate();
      }
    },
    removeActivityOutcomeRow: function(index: number) {
      if (this.createActivityRequest.outcomes !== undefined) {
        this.createActivityRequest.outcomes.splice(index, 1);
        this.$forceUpdate();
      }
      this.checkOutcomesLength();
    },
    addSelectedActivityType: async function() {
      if (!this.selectedActivityType) {
        return;
      }
      await activityController.addActivityType(
        this.selectedActivityType,
        this.createActivityRequest
      );
      this.selectedActivityType = "";
    },

    /*
    Opens the selected location on the map
    */
    viewOnMap: async function() {
      if(this.createActivityRequest.location === undefined || this.createActivityRequest.location === ""){
        this.errorMessage = "Please enter a location"
        return;
      }

      let res = await activityController.validateLocation(this.createActivityRequest.location);
      if(res === undefined || res[0] === undefined){
        this.errorMessage = "Specified location does not exist"
        return;
      }
      let pinLocation = {lon: parseFloat(res[0].lon), lat: parseFloat(res[0].lat)} as LocationCoordinatesInterface;
      let pinRequest = {geoposition: pinLocation, activity_id: -1} as CreateActivityRequest;
      this.createActivityRequest.location = res[0].display_name;
      this.$root.$emit('mapPaneToggle', true);
      this.$root.$emit('mapShowSearchResults', [pinRequest]);
      this.errorMessage = ""
    },
    removeActivityType: function(activityType: string) {
      activityController.removeActivityType(
        activityType,
        this.createActivityRequest
      );
    },

    cancelButtonClicked() {
      this.$router.back();
    },

    createButtonClicked: async function() {

      this.isSubmitting = true;

      if (this.createActivityRequest.outcomes === undefined) {
        this.createActivityRequest.outcomes = [];
      }
      this.createActivityRequest.outcomes = this.createActivityRequest.outcomes.filter(
        outcome => outcome.description && outcome.units
      );
      try {
        await activityController.validateNewActivity(
          this.startDate || "",
          this.startTime || "",
          this.endDate || "",
          this.endTime || "",
          this.createActivityRequest
        );
      } catch (err) {
        this.errorMessage = err.message;
        this.isSubmitting = false;
        return; // don't try to save the activity
      }
      activityController
        .editOrCreateActivity(
          this.createActivityRequest,
          this.currentProfileId,
          this.editingId,
          this.isEditing
        )
        .then(() => {
          this.$router.push({ name: "profilePage" });
        })
        .catch(err => {
          this.errorMessage = err.message;
          if (err.message.startsWith("cannot delete outcome")) {
            alert(err.message)
            history.go(0)
          }
        })
        .finally(() => {
          this.isSubmitting = false;
        });
    },

    deleteButtonClicked: async function() {
      this.confirmDeleteModal = false;
      activityController
        .deleteActivity(this.currentProfileId, this.editingId)
        .then(() => {
          this.$root.$emit('refreshPins');
          this.$router.push({ name: "profilePage" });
        })
        .catch(err => {
          alert("An error occured while deleting the activity:\n" + err);
        });
    },

    populateFields: async function(editingId: number) {
      let activityData: CreateActivityRequest = await activityController.getActivity(
        this.currentProfileId,
        editingId
      );
      this.createActivityRequest = activityData;
      if (
        this.createActivityRequest.start_time &&
        this.createActivityRequest.end_time
      ) {
        this.startDate = activityController.getDateFromISO(
          this.createActivityRequest.start_time
        );
        this.endDate = activityController.getDateFromISO(
          this.createActivityRequest.end_time
        );
        this.startTime = activityController.getTimeFromISO(
          this.createActivityRequest.start_time
        );
        this.endTime = activityController.getTimeFromISO(
          this.createActivityRequest.end_time
        );
      }
      //TODO populate date fields
    },
  },
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
