<template>
  <div style="text-align: center" id="ActivitySearchResults">
  <p class="pl-1" style="color: red">{{ errorMessage }}</p>

    <v-data-table
    onmouseover="" style="cursor: pointer;"
    :no-data-text="noDataText"
    :headers="headers"
    :items="results"
    item-key="activity_id"
    @click:row="goToActivity"
    :items-per-page="pageSize"
    single-select
    hide-default-footer
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
  props: ['results', 'pageSize'],

  data () {
    return {
      headers: [
        { text: 'Name', value: 'activity_name' },
        { text: 'Type', sortable: false, value: 'short_interests' },
        { text: 'Creator', value: 'creator_name' },
        { text: 'Location', value: 'location' },
        { text: 'Participant Count', value: 'num_participants' },
        { text: 'Follower Count', value: 'num_followers'}
      ],
      activities: [] as CreateActivityRequest[],
      selectedActivities: [] as CreateActivityRequest[],
      users: [] as UserApiFormat[],
      errorMessage: "",
      noDataText: "No activities",
      searchRulesModal: false,
      creatorId: NaN as number,
    }
  },

  methods: {
    /**
     * takes the user to the activity corresponding with the given search result.
     * @param activity the activity the user wants to navigate to
     */
    goToActivity: function(activity: CreateActivityRequest) {
      if (activity.creator_id === undefined || activity.activity_id === undefined) {
        return;
      }
      this.$router.push({ name: "activity", params: {
        profileId: activity.creator_id.toString(),
        activityId: activity.activity_id.toString()
      }});
    },

    /**
     * converts the given list of activity types as a single string.
     * @param activities the list of activity type names to put into a string
     */
    getActivitiesString(activities: string[]) {
      return getShortenedActivityTypesString(activities);
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
