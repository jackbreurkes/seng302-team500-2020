<template>
  <div id="Homefeed">
    <v-layout justify-center class="pt-1">
      <v-col>
        <div v-if="changeLogList.length > 0">
          <div v-for="item in changeLogList" :key="item.edited_timeStamp" class="ma-2">
            <HomeFeedCard v-bind:cardData="item">item.creatorName</HomeFeedCard>
          </div>
        </div>
        <div v-else>
          <v-layout justify-center class="pt-1">
            <v-card width="600" height="100%">
              <v-toolbar color="blue" dark flat height="50">
                <v-card-title>Welcome to your Intitulada homefeed</v-card-title>
              </v-toolbar>
              <v-spacer></v-spacer>
              <v-card-text class="pl-7 subtitle-1">To see more here, follow some activities you are interested in!</v-card-text>
            </v-card>
          </v-layout>
        </div>
      </v-col>
    </v-layout>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import HomeFeedCard from "./HomeFeedCard.vue";
import * as HomefeedController from "../controllers/homefeed.controller";
// eslint-disable-next-line no-unused-vars
import { HomeFeedCardType } from "../scripts/HomeFeedCardType";

const Homefeed = Vue.extend({
  name: "Homefeed",
  components: {HomeFeedCard},
  // app initial state
  data: function() {
    return {
      changeLogList: [] as HomeFeedCardType[],
      lastId: NaN as number,
      suggestions: [] as HomeFeedCardType[],
    };
  },
  created: async function() {

    this.changeLogList = await HomefeedController.getHomeFeedData();
    this.suggestions = await HomefeedController.getSuggestionsForHomeFeed();
    this.changeLogList = this.changeLogList.concat(this.suggestions);
    this.updateLastId();

  },
  methods: {
    /**
     * Sets the callback to be called anytime when the user scrolls the document.
     */
    setOnScroll: function() {
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
    this.setOnScroll()
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
