<template>
  <div>
    <div id="map" ref="map"></div>
  </div>
</template>

<script lang="ts">
  /**
   * Maps API: https://developers.google.com/maps/documentation/javascript/overview
   */

  import Vue from 'vue'

  // app Vue instance
  const MapView = Vue.extend({
    name: 'MapView',

    // app initial state
    data: function() {
      return {
          map: null,
      }
    },

    mounted() {
      // @ts-ignore next line
      this.map = new window.google.maps.Map(this.$refs["map"], {
        center: {
          lat: -43.525, 
          lng: 172.58
        },
        zoom: 4,
        streetViewControl: false
      })
    },

  })

  export default MapView
</script>

<style>
  [v-cloak] { display: none; }

#map {
  height: 100%;
  width: 100%;
  background: grey;
}
</style>
