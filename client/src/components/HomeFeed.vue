<template>
  <div id="Homefeed">
    <v-layout justify-center class="pt-1">
      <v-col>
        <div v-for="item in changeLogList" :key="item.edited_timeStamp" class="ma-2">
          <HomeFeedCard v-bind:cardData="item">item.creatorName</HomeFeedCard>
        </div>
      </v-col>
    </v-layout>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import HomeFeedCard from "./HomeFeedCard.vue"
import * as HomefeedController from '../controllers/homefeed.controller';
// eslint-disable-next-line no-unused-vars
import { HomeFeedCardType } from '../scripts/HomeFeedCardType';

const Homefeed = Vue.extend({
  name: "Homefeed",
  components: {HomeFeedCard},
  // app initial state
  data: function() {
    return {
      changeLogList: [] as HomeFeedCardType[]
    };
  },
  created: async function(){
    this.changeLogList = await HomefeedController.getHomeFeedData();

  },
  methods: {
  }
});

export default Homefeed;
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
