<template>
  <v-card>
    <div id="infowindow" ref="infowindow" v-show="this.openInfoWindow !== null">
      <div v-for="activity in displayedActivities" :key="activity.activity_id" class="ma-2">
        <MapInfoWindowView v-bind:activity="activity" v-on:clicked-goto-activity="visitActivity(activity)"></MapInfoWindowView>
      </div>
    </div>
    <div id="map" ref="map"></div>
    <!-- index of -1 below places the legend above the fullscreen button -->
    <div id="legend" ref="legend" class="ma-1 pa-1 subtitle-2 rounded white" index=-1>
      <h3 class="ma-0 pa-0">Legend</h3>
      <div v-for="icon in legend" :key="icon.title" class="ma-0 pa-0">
        <v-list-item-icon class="ma-0 pa-0">
          <p class="ma-0 pa-0 body-2"><v-icon :color="icon.colour">{{ icon.icon }}</v-icon>{{ icon.title }}</p>
        </v-list-item-icon>
      </div>
    </div>
    <div>
      <v-snackbar
        absolute
        bottom
        v-model="isShowingSearchResults"
        timeout="-1"
      >
        Showing {{storedSearchResultsPins.length}} search {{storedSearchResultsPins.length == 1 ? "result" : "results"}} on map

        <template v-slot:action="{ attrs }">
          <v-btn
            color="pink"
            text
            v-bind="attrs"
            @click="stopSearchResults()"
          >
            Stop filtering
          </v-btn>
        </template>
      </v-snackbar>
      <v-snackbar
        absolute
        bottom
        v-model="showingFiftyPins"
        timeout="-1"
      >
        There may be more activities in this area than currently shown
      </v-snackbar>
    </div>
  </v-card>
</template>

<script lang="ts">
  /**
   * Maps API: https://developers.google.com/maps/documentation/javascript/overview
   */

  import Vue from 'vue'
  import { getProfileLocation } from "../controllers/profile.controller";
  import { getActivitiesInBoundingBox, getActivityById} from "../controllers/activity.controller";
  import { getMyUserId } from "../services/auth.service"
  // eslint-disable-next-line no-unused-vars
  import { LocationCoordinatesInterface } from '../scripts/LocationCoordinatesInterface';
  // eslint-disable-next-line no-unused-vars
  import { BoundingBoxInterface } from '../scripts/BoundingBoxInterface';
  // eslint-disable-next-line no-unused-vars
  import { Pin } from '../scripts/Pin';
  // eslint-disable-next-line no-unused-vars
  import { CreateActivityRequest } from '../scripts/Activity';
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
          legend: {},
          isShowingSearchResults: false as boolean,
          storedSearchResultsPins: [] as Pin[],
          defaultLegend: {
            created: {
              title: 'Organising',
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
            },
            recommended : {
              title: 'Recommended',
              colour: 'rgba(0,230,77)',
              icon: 'mdi-square'
            }
          },
          searchResultLegend: {
            searchResult: {
              title: 'Search Result',
              colour: 'rgba(0, 230, 77, 1)',
              icon: 'mdi-square'
            },
          },
          loggedInUserId: NaN as number, //used to detect changes in authentication, i.e. center on a user when they log in
          displayedActivities: [] as CreateActivityRequest[], //activities being displayed in an active info window
          displayedMarkers: [] as any[], //'Marker' objects of pins being displayed
          openInfoWindow: null as any, //'InfoWindow' object of the activity summary popup by a pin
          mapIcons: [
            "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/purple-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/orange-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/green-dot.png",
            "https://maps.google.com/mapfiles/ms/icons/blue-dot.png"
          ], //in the order: creator/organiser, participant, following, recommended activity & search result, miscellaneous
          showingFiftyPins: false as boolean,
      }
    },

    mounted: async function() {
      this.$root.$on('showActivityOnMap', (activity : CreateActivityRequest) => {
        // @ts-ignore next line
        const mapWidth = this.$refs.map.clientWidth;
        // @ts-ignore next line
        const mapHeight = this.$refs.map.clientHeight;

        let shouldWait = mapWidth < 50 || mapHeight < 50;

        if (shouldWait) {
          this.$root.$emit('mapPaneToggle', true);
          setTimeout(() => {
            this.centerMapOnActivity(activity);
          }, 250)
        } else {
          this.centerMapOnActivity(activity);
        }
      })

      /**
       * This method listens for a call that refreshes the map and pins,
       * currently used for when the user updates their location
       */
      this.$root.$on('refreshMapAndPins', () => {
        this.centerMapOnUserLocation();
        this.displayPinsOnMap();
      })


      /**
       * This method listens for a call that refreshes pins,
       * currently used for when the user follows or unfollows etc
       */
      this.$root.$on('refreshPins', () => {
        this.displayPinsOnMap();
      })

      this.legend = this.defaultLegend;

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
          this.displayPinsOnMap();
        }, 300);
      });
      // @ts-ignore next line
      this.map.addListener('click', () => {
        if (this.openInfoWindow !== null) {
          this.openInfoWindow.close();
        }
      });

      this.$root.$on('mapShowSearchResults', (activityResults: CreateActivityRequest[]) => {
        let results = PinsController.convertToPins(activityResults);
        if (results.length == 0) {
          return;
        }
        this.legend = this.searchResultLegend;
        this.isShowingSearchResults = true;
        this.storedSearchResultsPins = results;
        this.displayPinsOnMap();

        let nePoint = {lat: results[0].coordinates.lat, lon: results[0].coordinates.lon} as LocationCoordinatesInterface;
        let swPoint = {lat: results[0].coordinates.lat, lon: results[0].coordinates.lon} as LocationCoordinatesInterface;

        for (let pin of results) {
          swPoint.lat = Math.min(pin.coordinates.lat, swPoint.lat);
          swPoint.lon = Math.min(pin.coordinates.lon, swPoint.lon);
          nePoint.lat = Math.max(pin.coordinates.lat, nePoint.lat);
          nePoint.lon = Math.max(pin.coordinates.lon, nePoint.lon);
        }

        // @ts-ignore next line
        this.map.fitBounds(PinsController.convertToGoogleBounds(swPoint, nePoint));
        // @ts-ignore next line
        if (this.$map.getZoom() >= 18) {
          // @ts-ignore next line
          this.$map.setZoom(18)
        }
      });
    },

    methods: {
      /**
       * Loads and displays all pins visible on the map
       */
      displayPinsOnMap: async function() {
        // @ts-ignore next line
        let bounds = this.map.getBounds();
        let boundingBox = PinsController.convertFromGoogleBounds(bounds);

        this.deletePinsOutsideBounds(boundingBox);

        let pins = this.isShowingSearchResults ? this.storedSearchResultsPins : await getActivitiesInBoundingBox(boundingBox);
        let pinsAtLocationMapping = PinsController.groupPinsByLocation(pins);
        let positionsOfNewPins = {} as Record<number, number[]>;

        this.showingFiftyPins = pins.length >= 50;

        //create pins on the map for each unique location
        pinsAtLocationMapping.forEach((pins: Pin[]) =>  {
          let position = {lat: pins[0].coordinates.lat, lon: pins[0].coordinates.lon} as LocationCoordinatesInterface;
          if (!(position.lat in positionsOfNewPins)) {
            positionsOfNewPins[position.lat] = [];
          }
          positionsOfNewPins[position.lat].push(position.lon);
          
          const epsilon = 0.00005;
          let potentialFoundMarker = this.displayedMarkers.find(element => {
            return Math.abs(element.getPosition().lat() - position.lat) < epsilon
            && Math.abs(element.getPosition().lng() - position.lon) < epsilon;
          });

          let highestRole = this.isShowingSearchResults ? 3 : PinsController.getHighestRoleIndex(pins);

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
       * Called when the user opts to stop showing their most recent activity search results on the map
       */
      stopSearchResults: function() {
        this.isShowingSearchResults = false;
        this.displayPinsOnMap();
        this.legend = this.defaultLegend;
      },

      /**
       * Gets the user's location and centers the map on this location
       */
      centerMapOnUserLocation: async function() {
        let userId = getMyUserId();
        if (userId === null) {
          return;
        }
        let location = await getProfileLocation(userId);
        if (location === null) {
          return;
        }
        // @ts-ignore next line
        this.map.setCenter({lat: location.lat, lng: location.lon})
        // @ts-ignore next line
        this.map.setZoom(11);
      },

      /**
       * Centers the map on the activity with an appropriate zoom level
       */
      centerMapOnActivity: function(activity: CreateActivityRequest) {
        if (activity.bounding_box === undefined ){
          return
        }
        let googleBounds = PinsController.convertToGoogleBounds(activity.bounding_box[0], activity.bounding_box[1])
        // @ts-ignore next line
        this.$map.fitBounds(googleBounds,50);
        // @ts-ignore next line
        if (this.$map.getZoom() >= 18) {
          // @ts-ignore next line
          this.$map.setZoom(18)
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
        if(allActivities.length === 1 && allActivities[0].activity_id === -1){
          return;
        }
        this.displayedActivities = [];
        for (let activityIndex in allActivities) {
          let activityId = allActivities[activityIndex];
          let activity;
          try {
            activity = await getActivityById(activityId.activity_id);
          } catch (e) {
            continue; // activity not loaded, do not attempt to display
          }
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
      $route(to , from) {
        let userId = getMyUserId();

        if (userId !== null && userId != this.loggedInUserId) {
          this.centerMapOnUserLocation();
          this.loggedInUserId = userId;
        }
        //Checks if an activity has been updated or created and refreshes the map pane
        if(from.name === 'createActivity' || from.name === 'editActivity'){
          this.displayedMarkers.forEach(marker => {marker.setMap(null)});
          this.displayedMarkers = [];
          this.displayPinsOnMap();
        }
      },
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
