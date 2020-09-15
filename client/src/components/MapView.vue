<template>
  <div>
    <div id="infowindow" ref="infowindow" v-show="this.openInfoWindow !== null">
      <v-container 
        v-for="activity in this.displayedActivities" 
        v-bind:key="activity.activity_id"
      >
        <v-row><b>{{activity.activity_name}}</b></v-row>
        <v-row no-gutters v-if="!activity.continuous">
          <v-col cols="6">Starts</v-col>
          <v-col cols="6">{{activity.start_time}}</v-col>
        </v-row>
        <v-row no-gutters v-if="!activity.continuous">
          <v-col cols="6">Ends</v-col>
          <v-col cols="6">{{activity.end_time}}</v-col>
        </v-row>
        <v-row no-gutters>
          <v-col cols="6">Activity Types</v-col>
          <v-col cols="6">{{activity.activity_type.join(", ")}}</v-col>
        </v-row>
        <v-row>
          <v-col cols="12"><v-btn @click="visitActivity(activity)" text small color="primary">Go to activity</v-btn></v-col>
        </v-row>
      </v-container>
    </div>
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
  import { getActivitiesInBoundingBox, getActivityById } from "../controllers/activity.controller";
  import { getMyUserId } from "../services/auth.service"
  // eslint-disable-next-line no-unused-vars
  import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';
  // eslint-disable-next-line no-unused-vars
  import { BoundingBoxInterface } from '@/scripts/BoundingBoxInterface';
  // eslint-disable-next-line no-unused-vars
  import { Pin } from '@/scripts/Pin';
  // eslint-disable-next-line no-unused-vars
  import { CreateActivityRequest } from '@/scripts/Activity';

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
          displayedPins: [] as any[],
          displayedActivities: [] as CreateActivityRequest[],
          openInfoWindow: null as any,
          mapIcons: [
            "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/purple-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/orange-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/blue-dot.png"
          ] //in the order: creator, participant, following, miscellaneous
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
        streetViewControl: false,
        clickableIcons: false
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
            "activity_id": 193,
            "location": {
              lat: -41.2784228,
              lon: 174.7766923
            },
            "role": "creator"
          },
          {
            "activity_id": 3,
            "location": {
              lat: -41.2784228,
              lon: 174.7766923
            },
            "role": "participant"
          },
          {
            "activity_id": 195,
            "location": {
              lat: -41.2774228,
              lon: 174.7766923
            },
            "role": "follower"
          }
        ] as Pin[];

        try {//TODO take out the try statement once the endpoint is implemented
          pins = await getActivitiesInBoundingBox(boundingBox);
        } catch (err) {
          1+1;
        }

        let createdPositions = [] as any[];

        //clear all the pins
        this.displayedPins.forEach((marker, index) => {
          for (let pinIndex in pins) {
            let pin = pins[pinIndex];
            if (pin.location.lat == marker.position.lat() && pin.location.lon == marker.position.lng()) {
              createdPositions.push({lat: pin.location.lat, lng: pin.location.lon});
              return;
            }
          }
          // @ts-ignore next line
          marker.setMap(null);
          delete this.displayedPins[index];
        });

        pins.forEach((pin: Pin) =>  {
          let position = {lat: pin.location.lat, lng: pin.location.lon};
          if (createdPositions.includes(position)) {
            return;
          }
          let allActivities = [] as number[];
          let highestRole = 3;
          
          pins.forEach((pin: Pin) =>  {
            if (pin.location.lat == position.lat && pin.location.lon == position.lng) {
              allActivities.push(pin.activity_id);
              let role = pin.role;
              if (role == "creator") {
                highestRole = 0;
              } else if (role == "participant" && highestRole > 1) {
                highestRole = 1;
              } else if (role == "follower" && highestRole > 2) {
                highestRole = 2;
              }
            }
          });
          // @ts-ignore next line
          let displayedPin = new window.google.maps.Marker({
            position: position, 
            map: this.map,
            icon: this.mapIcons[highestRole]
          });

          displayedPin.addListener('click', () => {
            this.createPinInfoWindow(this.map, displayedPin, allActivities);
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
      },

      createPinInfoWindow: async function(map: any, displayedPin: any, allActivities: number[]) {
        this.displayedActivities = [];
        for (let activityIndex in allActivities) {
          let activityId = allActivities[activityIndex];
          let activity = await getActivityById(activityId);
          this.displayedActivities.push(activity);
        }

        if (this.openInfoWindow !== null) {
          this.openInfoWindow.close();
          this.openInfoWindow = null;
        }
        
        // @ts-ignore next line
        let infoWindow = new window.google.maps.InfoWindow({
          "content": this.$refs["infowindow"]
        });
        infoWindow.open(map, displayedPin);
        this.openInfoWindow = infoWindow;
      },

      visitActivity: function(activity: CreateActivityRequest) {
        if (activity.activity_id == null || activity.creator_id == null) {
          return;
        }
        this.$router.push({ name: "activity", params: {
            profileId: activity.creator_id.toString(),
            activityId: activity.activity_id.toString()
          }
        });
        this.openInfoWindow.close();
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
