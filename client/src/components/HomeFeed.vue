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
        <v-layout justify-center class="pt-1">
          <v-btn :loading="loadingMore" @click="goToSearchPage" id="loadMoreButton">
            <!-- show no text if loading to minimise button width -->
            {{ loadingMore ? "" : "search for other activities" }}
          </v-btn>
        </v-layout>
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
      loadingMore: false as boolean,
      observer: null as IntersectionObserver | null,
    };
  },
  created: async function() {
    const suggestions = await HomefeedController.getSuggestionsForHomeFeed();
    const homeFeed = await HomefeedController.getHomeFeedData();
    this.changeLogList.push(...suggestions);
    this.changeLogList.push(...homeFeed);

    let target = document.querySelector('#loadMoreButton');
    if (target !== null && this.observer !== null) {
      this.observer.observe(target);
    }
  },
  methods: {
    loadMore: function() {
      this.loadingMore = true;
      HomefeedController.getAdditionalUsersHomefeed(this.lastId)
        .then(response => {
            this.changeLogList.push.apply(this.changeLogList, response)
            this.updateLastId();
        })
        .finally(() => {
          this.loadingMore = false;
        });
    },
    /**
     * takes the user to the activity search page.
     */
    goToSearchPage() {
      this.$router.push({ name: "activities" });
    },
    updateLastId: function() {
      this.lastId = this.changeLogList[this.changeLogList.length -1].change_id;
    }
  },
  mounted: function() {
    // uses IntersectionObserver API built into the browser
    // https://developer.mozilla.org/en-US/docs/Web/API/Intersection_Observer_API
    this.observer = new IntersectionObserver((event) => {
      if (event[0] !== undefined && event[0].isIntersecting) { //if the button is now in view
        this.loadMore();
      }  
    }, {threshold: 0.5});
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
