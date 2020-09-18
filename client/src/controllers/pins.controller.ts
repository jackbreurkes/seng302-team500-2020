import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';
import { Pin } from '@/scripts/Pin';
import { BoundingBoxInterface } from '@/scripts/BoundingBoxInterface';

/**
 * sorts all of the pins into an array of pins at the same location
 * @param pins All of the unsorted pins
 * @returns Array of arrays of pins, where the inner arrays are activities occuring at the same location
 */
export function sortPinsByLocation(pins: Pin[]) {
    let uniquePinLocations = [] as LocationCoordinatesInterface[];
    let locationToActivityPinMapping = {} as Record<number, Pin[]>;

    //find unique locations
    for (let pinIndex in pins) {
        let pin = pins[pinIndex] as Pin;
        let position = {lat: pin.location.lat, lon: pin.location.lon} as LocationCoordinatesInterface;
        
        let potentialDuplicate = uniquePinLocations.find(element => element.lat == position.lat && element.lon == position.lon);
        if (potentialDuplicate === undefined) {
        uniquePinLocations.push(position);
        locationToActivityPinMapping[uniquePinLocations.length - 1] = [pin];
        } else {
        let index = uniquePinLocations.indexOf(potentialDuplicate);
        locationToActivityPinMapping[index].push(pin);
        }
    }

    return Object.values(locationToActivityPinMapping);
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
    let highestRole = 3;
    pins.forEach((pin: Pin) =>  {
        let role = pin.role;
        if (role == "creator" || role == "organiser") {
            highestRole = 0;
        } else if (role == "participant" && highestRole > 1) {
            highestRole = 1;
        } else if (role == "follower" && highestRole > 2) {
            highestRole = 2;
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