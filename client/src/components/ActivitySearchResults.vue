<template>
  <div style="text-align: center" id="ActivitySearchResults">
  <p class="pl-1" style="color: red">{{ errorMessage }}</p>

    <v-data-table
    :no-data-text="noDataText"
    :headers="headers"
    :items="results"
    item-key="activity_id"
    @click:row="goToActivity"
    :page.sync="page"
    single-select
    >
    <template #item.short_interests="{ item }">{{getActivitiesString(item.activity_type)}}</template>
    <template v-slot:items="activities">
      <td class="text-xs-right">{{ activities.item.activity_name }}</td>
      <td class="text-xs-right">{{ activities.item.short_interests }}</td>
      <td class="text-xs-right">{{ activities.item.creator_name }}</td>
      <td class="text-xs-right">{{ activities.item.location }}</td>
      <td class="text-xs-right">{{ activities.item.num_participants }}</td>
      <td class="text-xs-right">{{ activities.item.num_followers }}</td>
    </template>
  </v-data-table>

  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { getShortenedActivityTypesString } from '../controllers/activitySearch.controller';
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "@/scripts/User";
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '@/scripts/Activity'
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

// app Vue instance
const ActivitySearchResults = Vue.extend({
  name: "ActivitySearchResults",
  props: ['results'],

  data () {
    return {
      headers: [
        { text: 'Name', value: 'activity_name' },
        { text: 'Type', sortable: false, value: 'short_interests' },
        { text: 'Creator', value: 'creator_name' },
        { text: 'Location', value: 'location' },
        { text: 'Particpant Count', value: 'num_participants' },
        { text: 'Follower Count', value: 'num_followers'}
      ],
      activities: [] as CreateActivityRequest[],
      selectedActivities: [] as CreateActivityRequest[],
      users: [] as UserApiFormat[],
      errorMessage: "",
      noDataText: "No activities",
      searchRulesModal: false,
      creatorId: NaN as number,
      page: 1,
    }
  },

  computed: {
    
  },


  methods: {
    goToActivity: function(activity: CreateActivityRequest & {creator_name : string}) {
      if (activity.creator_id === undefined || activity.activity_id === undefined) {
        return;
      }
      this.$router.push(`/profiles/${activity.creator_id}/activities/${activity.activity_id}`);
    },

    getActivitiesString(activities: string[]) {
      return getShortenedActivityTypesString(activities);
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
