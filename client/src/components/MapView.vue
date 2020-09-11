<template>
  <div>
    <div id="map" ref="map"></div>
    <!-- index of -1 below places the legend above the fullscreen button -->
    <div id="legend" ref="legend" class="ma-1 pa-1 rounded white" index=-1>
      <h3 class="ma-0 pa-0">Legend</h3>
      <div v-for="icon in legend" :key="icon.title" class="ma-0 pa-0">
        <v-list-item-icon class="ma-0 pa-0">
          <p class="ma-0 pa-0"><v-icon small :color="icon.colour">{{ icon.icon }}</v-icon>{{ icon.title }}</p>
        </v-list-item-icon>
      </div>
    </div>
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
          legendCurrentlyOnLeft: true as boolean,
          legend: {
            created: {
              title: 'Created',
              colour: 'rgba(255, 0, 0, 1)',
              icon: 'mdi-square'
            },
            following: {
              title: 'Following',
              colour: 'rgba(255, 145, 0, 1)',
              icon: 'mdi-square'
            },
            participating: {
              title: 'Participating',
              colour: 'rgba(162, 0, 255, 1)',
              icon: 'mdi-square'
            },
            miscellaneous: {
              title: 'Miscellaneous',
              colour: 'rgba(120, 144, 156, 1)',
              icon: 'mdi-square'
            }
          }
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

      // Places the legend in the top right-hand corner
      // @ts-ignore next line
      this.map.controls[window.google.maps.ControlPosition.TOP_RIGHT].push(this.$refs['legend']);
    }

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
#legend {
    font-family: Arial, sans-serif;
    background: #fff;
    padding: 10px;
    margin: 10px;
    border: 3px solid #000;
  }
</style>
