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
  import { getProfileLocation } from "../controllers/profile.controller";
  import { getMyUserId } from "../services/auth.service"
  // eslint-disable-next-line no-unused-vars
  import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';

  // app Vue instance
  const MapView = Vue.extend({
    name: 'MapView',

    // app initial state
    data: function() {
      return {
          map: null,
      }
    },

    mounted: async function() {
      // @ts-ignore next line
      this.map = new window.google.maps.Map(this.$refs["map"], {
        center: {
          lat: -43.525, 
          lng: 172.58
        },
        zoom: 4,
        streetViewControl: false
      })
      Vue.prototype.$map = this.map; //make this globally accessible

      let userId = getMyUserId();
      let location = {lat: -43.5948293, lon: 172.4718623} as LocationCoordinatesInterface;
      if (userId !== null) {
        location = await getProfileLocation(userId);
      }

      // @ts-ignore next line
      this.map.setCenter({lat: location.lat, lng: location.lon})
      // @ts-ignore next line
      this.map.setZoom(11);
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
