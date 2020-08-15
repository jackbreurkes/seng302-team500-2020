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
      changeLogList: [] as HomeFeedCardType[],
      lastId: NaN as number
    };
  },
  created: async function() {
    this.changeLogList = await HomefeedController.getHomeFeedData();
    this.updateLastId();

  },
  methods: {
    scroll: function() {
      window.onscroll = () => {
        
        let bottomOfWindow = document.documentElement.scrollTop + window.innerHeight === document.documentElement.offsetHeight;

        if (bottomOfWindow) {
          HomefeedController.getAdditionalUsersHomefeed(this.lastId)
            .then(response => {
              this.changeLogList.push.apply(this.changeLogList, response)
              this.updateLastId();
            });
        }
      };
    },
    updateLastId: function() {
      this.lastId = this.changeLogList[this.changeLogList.length -1].change_id;
    }
  },
  mounted() {
    this.scroll()
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
