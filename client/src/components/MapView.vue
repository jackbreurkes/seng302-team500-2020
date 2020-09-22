<template>
  <div>
    <div id="infowindow" ref="infowindow" v-show="this.openInfoWindow !== null">
      <div v-for="activity in displayedActivities" :key="activity.activity_id" class="ma-2">
        <MapInfoWindowView v-bind:activity="activity" v-on:clicked-goto-activity="visitActivity(activity)"></MapInfoWindowView>
      </div>
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
  import * as PinsController from '../controllers/pins.controller';
  import MapInfoWindowView from './MapInfoWindowView.vue';

  // app Vue instance
  const MapView = Vue.extend({
    name: 'MapView',
    components: {MapInfoWindowView},
    // app initial state
    data: function() {
      return {
          map: null,
          legendCurrentlyOnLeft: true as boolean,
          legend: {
            created: {
              title: 'Created',
              colour: 'rgba(253, 117, 103, 1)',
              icon: 'mdi-square'
            },
            following: {
              title: 'Following',
              colour: 'rgba(255, 153, 0, 1)',
              icon: 'mdi-square'
            },
            participating: {
              title: 'Participating',
              colour: 'rgba(142, 103, 253, 1)',
              icon: 'mdi-square'
            },
            miscellaneous: {
              title: 'Miscellaneous',
              colour: 'rgba(105, 145, 253, 1)',
              icon: 'mdi-square'
            }
          },
          loggedInUserId: NaN as number, //used to detect changes in authentication, i.e. center on a user when they log in
          displayedActivities: [] as CreateActivityRequest[], //activities being displayed in an active info window
          displayedMarkers: [] as any[], //'Marker' objects of pins being displayed
          openInfoWindow: null as any, //'InfoWindow' object of the activity summary popup by a pin
          mapIcons: [
            "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/purple-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/orange-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/blue-dot.png"
          ] //in the order: creator/organiser, participant, following, miscellaneous
      }
    },

    mounted: async function() {
      // @ts-ignore next line
      this.map = new window.google.maps.Map(this.$refs["map"], {
        center: {
          lat: 0, 
          lng: 0
        },
        zoom: 3,
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
          let boundingBox = PinsController.convertFromGoogleBounds(bounds);

          this.displayPinsInArea(boundingBox);
        }, 300);
      });
      // @ts-ignore next line
      this.map.addListener('click', () => {
          if (this.openInfoWindow !== null) {
            this.openInfoWindow.close();
          }
        });
    },

    methods: {
      /**
       * Loads and displays all pins in the area defined by the bounding box
       * @param boundingBox The bounding box to display the pins inside of
       */
      displayPinsInArea: async function(boundingBox: BoundingBoxInterface) {
        this.deletePinsOutsideBounds(boundingBox);

        let pins = await getActivitiesInBoundingBox(boundingBox);
        let pinsAtLocationMapping = PinsController.groupPinsByLocation(pins);
        let positionsOfNewPins = {} as Record<number, number[]>;

        //create pins on the map for each unique location
        pinsAtLocationMapping.forEach((pins: Pin[]) =>  {
          let position = {lat: pins[0].coordinates.lat, lon: pins[0].coordinates.lon} as LocationCoordinatesInterface;
          if (!(position.lat in positionsOfNewPins)) {
            positionsOfNewPins[position.lat] = [];
          }
          positionsOfNewPins[position.lat].push(position.lon);
          
          let potentialFoundMarker = this.displayedMarkers.find(element => element.getPosition().lat() == position.lat && element.getPosition().lng() == position.lon);
          let highestRole = PinsController.getHighestRoleIndex(pins);

          if (potentialFoundMarker !== undefined) {
            let currentIcon = potentialFoundMarker.getIcon();
            let correctIcon = this.mapIcons[highestRole];
            if (currentIcon != correctIcon) {
              potentialFoundMarker.setIcon(correctIcon);
            }
            return; // this pin is already being displayed so no point recreating it
          }
          
          this.displayPin(pins, position, highestRole);
        })

        this.displayedMarkers = this.displayedMarkers.filter((marker) => {
          let position = {lat: marker.position.lat(), lon: marker.position.lng()} as LocationCoordinatesInterface;
          const shouldKeep = (position.lat in positionsOfNewPins && positionsOfNewPins[position.lat].includes(position.lon));
          if (!shouldKeep) {
            // @ts-ignore next line
            marker.setMap(null);
          }
          return shouldKeep;
        })
      },

      /**
       * Gets the user's location and centers the map on this location
       */
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

      /**
       * Create an info window for the specified pin, representing the activities.
       * This will also trigger Vue to load information about these activities
       * @param map The instance of the google maps object (google.maps.Map) that the info window should created on
       * @param displayedPin The instance of the marker (google.maps.Marker) that the info window should be anchored to
       * @param allActivities The list of pins representing activities happening at the location of the displayed pin
       */
      createPinInfoWindow: async function(map: any, displayedPin: any, allActivities: Pin[]) {
        this.displayedActivities = [];
        for (let activityIndex in allActivities) {
          let activityId = allActivities[activityIndex];
          let activity = await getActivityById(activityId.activity_id);
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

      /**
       * Tells Vue to navigate the map panel to the given activity
       */
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
      },

      /**
       * Deletes pins that are outside what's visible on the map view
       */
      deletePinsOutsideBounds: function(boundingBox: BoundingBoxInterface) {
        this.displayedMarkers = this.displayedMarkers.filter((marker) => {
          let position = {lat: marker.position.lat(), lon: marker.position.lng()} as LocationCoordinatesInterface;
          const shouldDelete = !PinsController.isInBounds(boundingBox, position);
          if (shouldDelete) {
            // @ts-ignore next line
            marker.setMap(null);
          }
          return !shouldDelete;
        });
      },

      /**
       * Constructs and displays a pin with the given parameters on the map
       */
      displayPin: function(pins: Pin[], position: LocationCoordinatesInterface, highestRole: number) {
        // @ts-ignore next line
        let displayedMarker = new window.google.maps.Marker({
          position: {lat: position.lat, lng: position.lon}, 
          map: this.map,
          icon: this.mapIcons[highestRole]
        });

        displayedMarker.addListener('click', () => {
          this.createPinInfoWindow(this.map, displayedMarker, pins);
        });

        this.displayedMarkers.push(displayedMarker);
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
