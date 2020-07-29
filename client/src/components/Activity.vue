<template>
  <h1> Congrats! You have made it to an activity page! </h1>
</template>

<script lang="ts">
import Vue from "vue";

// app Vue instance
const Activity = Vue.extend({
  name: "Activity",

  // app initial state
  data: function() {
    return {};
  }

});

export default Activity;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
