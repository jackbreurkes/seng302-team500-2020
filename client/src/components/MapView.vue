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
  import { getActivitiesInBoundingBox } from "../controllers/activity.controller";
  import { getMyUserId } from "../services/auth.service"
  // eslint-disable-next-line no-unused-vars
  import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';
  // eslint-disable-next-line no-unused-vars
  import { BoundingBoxInterface } from '@/scripts/BoundingBoxInterface';
  // eslint-disable-next-line no-unused-vars
  import { Pin } from '@/scripts/Pin';

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
          },
          loggedInUserId: NaN as number, //used to detect changes in authentication, i.e. center on a user when they log in
          displayedPins: [] as any[]
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

      this.centerMapOnUserLocation();

      // Places the legend in the top right-hand corner
      // @ts-ignore next line
      this.map.controls[window.google.maps.ControlPosition.TOP_RIGHT].push(this.$refs['legend']);

      /*
        everything below related to timeouts is to prevent sending tens of requests per second
        since the 'idle' event is called when the mapview has been resized, in addition to when
        the user has stopped dragging the map round. This throttles the client to only send
        a request every 300 milliseconds.

      */
      let timerId = -1;

      // @ts-ignore next line
      this.map.addListener('idle', () => {
        if (timerId !== -1) {
          clearTimeout(timerId);
        }
        timerId = setTimeout(() => {
          // @ts-ignore next line
          let bounds = this.map.getBounds();

          let boundingBox = {
            sw_lat: bounds['Va']['i'],
            ne_lat: bounds['Va']['j'],
            sw_lon: bounds['ab']['i'],
            ne_lon: bounds['ab']['j']
          } as BoundingBoxInterface;

          this.loadPinsInArea(boundingBox);
        }, 300);
      });
    },

    methods: {
      loadPinsInArea: async function(boundingBox: BoundingBoxInterface) {
        let pins = [ //for testing purposes
          {
            "activityId": 1,
            "location": {
              lat: -41.2784228,
              lon: 174.7766923
            }
          },
          {
            "activityId": 2,
            "location": {
              lat: -41.2784228,
              lon: 174.7766923
            }
          },
          {
            "activityId": 3,
            "location": {
              lat: -41.2774228,
              lon: 174.7766923
            }
          }
        ] as Pin[];

        try {//TODO take out the try statement once the endpoint is implemented
          pins = await getActivitiesInBoundingBox(boundingBox);
        } catch (err) {
          1+1;
        }

        //clear all the pins
        this.displayedPins.forEach((marker, index) => {
          // @ts-ignore next line
          marker.setMap(null);
          delete this.displayedPins[index];
        });

        let createdPositions = [] as any[];

        pins.forEach((pin: Pin) =>  {
          let position = {lat: pin.location.lat, lng: pin.location.lon};
          if (createdPositions.includes(position)) {
            return;
          }
          let allActivities = [] as any[];
          
          pins.forEach((pin: Pin) =>  {
            if (pin.location.lat == position.lat && pin.location.lon == position.lng) {
              allActivities.push(pin.activityId);
            }
          });
          // @ts-ignore next line
          let displayedPin = new window.google.maps.Marker({position: position, map: this.map});

          displayedPin.addListener('click', () => {
            alert("All activities available at this point: "+JSON.stringify(allActivities));
            //todo create https://developers.google.com/maps/documentation/javascript/reference/info-window
          });

          this.displayedPins.push(displayedPin);
          createdPositions.push(position);
        })
      },

      centerMapOnUserLocation: async function() {
        let userId = getMyUserId();

        if (userId !== null) {
          let location = await getProfileLocation(userId);
          // @ts-ignore next line
          this.map.setCenter({lat: location.lat, lng: location.lon})
          // @ts-ignore next line
          this.map.setZoom(11);
        }
      }
    },

    watch: {
      $route() {
        let userId = getMyUserId();

        if (userId !== null && userId != this.loggedInUserId) {
          this.centerMapOnUserLocation();
          this.loggedInUserId = userId;
        }
      }
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
