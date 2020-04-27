<template>
  <div id="CreateActivity">
    <v-form submit="false">
      <v-container class="fill-height" fluid>
        <v-row
        align="center"
        justify="center"
        >
          <v-col
          cols="12"
          sm="8"
          md="4"
          >
            <v-card class="elevation-12" width=100%>
              <v-toolbar
                color="primary"
                dark
                flat
              >
              <v-toolbar-title>CreateActivity:</v-toolbar-title>   
              </v-toolbar>
              <v-card-text>
                <v-text-field label="Activity Name" ref="activityname" id="activityname" type="text" v-model="activityname" ></v-text-field>
                <div>
                    <input type="radio" id="continuous" value="continuous" v-model="time"> <label class="pr-1" for="continuous">Continuous</label>
                    <input type="radio" id="duration" value="duration" v-model="time"> <label for="duration">Duration</label>
                    <br>
                    <p>Time Frame: {{ time }}</p>
                    <br>
                </div>
                <div v-if="time === 'duration'">
                    <v-text-field label="Start Date" ref="startDate" id="startDate" type="date" v-model="startDate" ></v-text-field>
                    <v-text-field label="Start Time" ref="startTime" id="startTime" type="time" v-model="startTime" ></v-text-field>
                    <v-text-field label="End Date" ref="endDate" id="endDate" type="date" v-model="endDate" ></v-text-field>
                    <v-text-field label="End Time" ref="endTime" id="endTime" type="time" v-model="endTime" ></v-text-field>
                </div>
                <v-textarea label="Describe your activity" ref="description" id="description" v-model="description" outlined > </v-textarea>
                <v-text-field label="Location" ref="location" id="location" type="text" v-model="location"></v-text-field>
                <v-text-field label="Activity Type" ref="activityType" id="activityType" v-model="activityType"></v-text-field>
              </v-card-text>
              <v-card-actions>
                <p class="pl-1" style="color: red">{{ errorMessage }}</p>
                <v-btn @click="cancelButtonClicked">Cancel</v-btn>
                <v-spacer />
                <v-btn @click="saveButtonClicked" color="primary" >Create</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-form>
  </div>
</template>


<script lang="ts">
// eslint-disable-next-line no-unused-vars
import Vue from 'vue'

// app Vue instance
const CreateActivity = Vue.extend({
  name: "CreateActivity",
  el: "#CreateActivity",

  // app initial state
  data: function() {
    return {
      activityName: "",
      time: "",
      description: "",
      location: "",
      activityType: "",
      startDate: "",
      startTime: "",
      endDate: "",
      endTime: "",

      errorMessage: ""
    };
  },

  methods: {
    cancelButtonClicked() {
        this.$router.push({ name: "profilePage" });
    },

    saveButtonClicked() {

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
