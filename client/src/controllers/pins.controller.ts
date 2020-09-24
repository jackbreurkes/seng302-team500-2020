import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';
import { Pin } from '@/scripts/Pin';
import { BoundingBoxInterface } from '@/scripts/BoundingBoxInterface';
import { CreateActivityRequest } from '@/scripts/Activity'

/**
 * groups all of the pins into an array of pins at the same location
 * @param pins All of the unsorted pins
 * @returns Array of arrays of pins, where the inner arrays are activities occuring at the same location
 */
export function groupPinsByLocation(pins: Pin[]) {
    let uniquePinLocations = {} as Record<number, Record<number, Pin[]>>; // first key is lat, second is lon

    // find unique locations
    for (let pinIndex in pins) {
        let pin = pins[pinIndex];
        let position = {lat: pin.coordinates.lat, lon: pin.coordinates.lon} as LocationCoordinatesInterface;
        
        if (position.lat in uniquePinLocations && position.lon in uniquePinLocations[position.lat]) {
            uniquePinLocations[position.lat][position.lon].push(pin);
        } else if (position.lat in uniquePinLocations) {
            uniquePinLocations[position.lat][position.lon] = [pin];
        } else {
            let lonsAtLat = {} as Record<number, Pin[]>;
            lonsAtLat[position.lon] = [pin];
            uniquePinLocations[position.lat] = lonsAtLat;
        }
    }

    let pinsGroupedByLocation = [] as Pin[][];
    for (let lat of Object.keys(uniquePinLocations)) {
        for (let lon of Object.keys(uniquePinLocations[parseFloat(lat)])) {
            pinsGroupedByLocation.push(uniquePinLocations[parseFloat(lat)][parseFloat(lon)]);
        }
    }

    return pinsGroupedByLocation;
}

/**
 * Gets the highest role in the order of: creator/organiser, participant, follower and returns its ID from:
 * creator or organiser: 0,
 * participant: 1,
 * follower: 2,
 * otherwise: 3
 * Used for colour coding the map
 * @param pins List of pins at the same location
 * @returns role index of highest ranking role of the given pins
 */
export function getHighestRoleIndex(pins: Pin[]) {
    let highestRole = 4;
    pins.forEach((pin: Pin) =>  {
        let role = pin.role;
        if (role == "creator" || role == "organiser") {
            highestRole = 0;
        } else if (role == "participant" && highestRole > 1) {
            highestRole = 1;
        } else if (role == "follower" && highestRole > 2) {
            highestRole = 2;
        } else if (pin.is_recommended === true && highestRole > 3) {
            highestRole = 3;
        }
    });
    return highestRole;
}

/**
 * Converts the google maps bounding box to our BoundingBoxInterface format
 * @param bounds Google maps bounding box
 * @return BoundingBoxInterface representing the same bounding box
 */
export function convertFromGoogleBounds(bounds: any) {
    let northEast = bounds.getNorthEast();
    let southWest = bounds.getSouthWest();

    return {
        sw_lat: southWest.lat(),
        ne_lat: northEast.lat(),
        sw_lon: southWest.lng(),
        ne_lon: northEast.lng()
    } as BoundingBoxInterface;
}

/**
 * Converts both the NE and SW points to google maps api format boundingbox
 * @param swPoint the SW point of the bounding box for the location 
 * @param nePoint the NE point of the bounding box for the location
 * @return google maps api BoundingBox format given by location boundingbox
 */
export function convertToGoogleBounds(swPoint: LocationCoordinatesInterface, nePoint: LocationCoordinatesInterface) {

    let northEast = {lat: nePoint.lat, lng: nePoint.lon};
    let southWest = {lat: swPoint.lat, lng: swPoint.lon};
    // @ts-ignore next line
    return new window.google.maps.LatLngBounds(southWest, northEast)
}

/**
 * Returns whether the location falls within the given bounding box
 * @param boundingBox The bounding box to compare the location with
 * @param location The location to test if it falls within the bounding box
 * @returns true if the location is within the bounds of the given bounding box
 */
export function isInBounds(boundingBox: BoundingBoxInterface, location: LocationCoordinatesInterface) {
    return location.lat >= boundingBox.sw_lat && location.lat <= boundingBox.ne_lat
        && location.lon >= boundingBox.sw_lon && location.lon <= boundingBox.ne_lon;
}

/**
 * Convert a list of activities into a list of pins
 * @param activityList List of activities to convert
 * @returns list of pins representing the activities
 */
export function convertToPins(activityList: CreateActivityRequest[]) {
    let output = [];

    for (let activity of activityList) {
        if (activity.geoposition !== undefined) {
            let pin = {
                activity_id: activity.activity_id,
                coordinates: activity.geoposition
            } as Pin;
            output.push(pin);   
        }
    }

    return output;
}