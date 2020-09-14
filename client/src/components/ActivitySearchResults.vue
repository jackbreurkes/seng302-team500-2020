<template>
  <div style="text-align: center" id="ActivitySearchResults">
  <p class="pl-1" style="color: red">{{ errorMessage }}</p>

    <v-data-table
    :no-data-text="noDataText"
    :headers="headers"
    :items="activities"
    item-key="activity_id"
    @click:row="goToActivity"
    :page.sync="page"
    single-select
    v-model="selectedActivities"
    >
    <template #item.full_name="{ item }">{{ item.activityName }} {{ item.creatorName }} {{ item.location }}</template>
    <template #item.short_interests="{ item }">{{getActivitiesString(item.activities)}}</template>
    <template v-slot:items="activities">
      <td class="text-xs-right">{{ activities.item.activityName }}</td>
      <td class="text-xs-right">{{ activities.item.short_interests }}</td>
      <td class="text-xs-right">{{ activities.item.creatorName }}</td>
      <td class="text-xs-right">{{ activities.item.location }}</td>
      <td class="text-xs-right">{{ activities.item.participants }}</td>
      <td class="text-xs-right">{{ activities.item.followers }}</td>
    </template>
  </v-data-table>

  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { searchActivities } from '../controllers/activitySearch.controller';
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "@/scripts/User";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '@/scripts/Activity'
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

// app Vue instance
const ActivitySearchResults = Vue.extend({
  name: "ActivitySearchResults",
  props: ['searchTerms'],
  watch: {
    searchTerms(newValue) {
      this.search(newValue);
    }
  },
  data () {
    return {
      headers: [
        { text: 'Name', value: 'activityName' },
        { text: 'Type', sortable: false, value: 'short_interests' },
        { text: 'Creator', value: 'creatorName' },
        { text: 'Location', value: 'location' },
        { text: 'Particpant Count', value: 'participants' },
        { text: 'Follower Count', value: 'followers'}
      ],
      activities: [] as CreateActivityRequest[],
      selectedActivities: [] as CreateActivityRequest[],
      users: [] as UserApiFormat[],
      errorMessage: "",
      noDataText: "No users found",
      searchRulesModal: false,
      creatorId: NaN as number,
      page: 1
    }
  },
  created: async function() {
    await this.search(this.searchTerms);
    this.checkPage();
  },

  computed: {
    selectedActivity: function() {
      if (this.selectedActivities.length !== 1){ 
        return null;
      }
      let selectedActivities: CreateActivityRequest = this.selectedActivities[0];
      return selectedActivities;
    }
  },


  methods: {
    goToActivity: function(activityId: any) {
      this.$router.push(`/profiles/${this.creatorId}/activities/${activityId}`);
    },
    search: async function(searchTerms: string[]) {
      this.noDataText = "No activities found";
      this.errorMessage = "";
      try {
        let activities = await searchActivities(searchTerms)
        this.activities = activities as CreateActivityRequest[];
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
        this.activities = [];
      }
    },

    checkPage: function(){
      if(localStorage.getItem("searchPage")){
        this.page = parseInt(localStorage.getItem("searchPage")!);
        localStorage.removeItem("searchPage");
      }
    },

  },
});
    
export default ActivitySearchResults;
</script>

<style>
[v-cloak] {
  display: none;
}
p {
  display: inline-block;
}

.admin-controls {
  height: 50px;
}
</style>
