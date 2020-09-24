import { BoundingBoxInterface } from '@/scripts/BoundingBoxInterface';
import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';
import { CreateActivityRequest } from '@/scripts/Activity';
import { Pin } from "@/scripts/Pin";
import * as PinsController from '../controllers/pins.controller';

// --------- SORT PINS BY LOCATION ---------- //
test('expect 1 location to be sorted', 
    () => {
        let pin = {activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface
        } as Pin;

        let result = PinsController.groupPinsByLocation([pin]);

        expect(result).toHaveLength(1);
        expect(result[0]).toHaveLength(1);
        expect(result[0][0]).toBe(pin);
    }
)

test('expect 2 locations with different lat to be sorted as different locations', 
    () => {
        let pin = {activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface
        } as Pin;
        let pin2 = {activity_id: 1,
            coordinates: {lat: 11, lon: 10} as LocationCoordinatesInterface
        } as Pin;

        let result = PinsController.groupPinsByLocation([pin, pin2]);

        expect(result).toHaveLength(2);
    }
)

test('expect 2 locations with different lon to be sorted as different locations', 
    () => {
        let pin = {activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface
        } as Pin;
        let pin2 = {activity_id: 1,
            coordinates: {lat: 10, lon: 11} as LocationCoordinatesInterface
        } as Pin;

        let result = PinsController.groupPinsByLocation([pin, pin2]);

        expect(result).toHaveLength(2);
    }
)

test('expect 2 identical locations to be grouped', 
    () => {
        let pin = {activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface
        } as Pin;
        let pin2 = {activity_id: 1,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface
        } as Pin;

        let result = PinsController.groupPinsByLocation([pin, pin2]);

        expect(result).toHaveLength(1);
        expect(result[0]).toHaveLength(2);
    }
)

// --------- GET HIGHEST ROLE ---------- //
test('expect activity we created to give creator role (0) as highest role', 
    () => {
        let pin = {
            activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
            role: "creator"
        } as Pin;

        let result = PinsController.getHighestRoleIndex([pin]);

        expect(result).toBe(0);
    }
)

test('expect activity we organise to give creator role (0) as highest role', 
    () => {
        let pin = {
            activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
            role: "organiser"
        } as Pin;

        let result = PinsController.getHighestRoleIndex([pin]);

        expect(result).toBe(0);
    }
)

test('expect activity we participate in to give participant role (1) as highest role', 
    () => {
        let pin = {
            activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
            role: "participant"
        } as Pin;

        let result = PinsController.getHighestRoleIndex([pin]);

        expect(result).toBe(1);
    }
)

test('expect activity we follow to give follower role (2) as highest role', 
    () => {
        let pin = {
            activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
            role: "follower"
        } as Pin;

        let result = PinsController.getHighestRoleIndex([pin]);

        expect(result).toBe(2);
    }
)

test('expect activity we are recommended to give recommended role (3) as highest role', 
    () => {
        let pin = {
            activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
            role: null,
            is_recommended: true
        } as Pin;

        let result = PinsController.getHighestRoleIndex([pin]);

        expect(result).toBe(3);
    }
)

test('expect activity we have no role in to give misc role (4) as highest role', 
    () => {
        let pin = {
            activity_id: 0,
            coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
            role: null
        } as Pin;

        let result = PinsController.getHighestRoleIndex([pin]);

        expect(result).toBe(4);
    }
)

let roleMap = {"creator": 0, "organiser": 0, "participant": 1, "follower": 2, "null": 4} as Record<string, number>;

for (let [roleName, roleLevel] of Object.entries(roleMap)) {
    for (let [roleName2, roleLevel2] of Object.entries(roleMap)) {
        let correctResult = roleLevel < roleLevel2 ? roleLevel : roleLevel2;
        test('expect activity we have '+roleName+' role and activity we have '+roleName2+' role in to give role ID '+correctResult+' as highest role', 
            () => {
                let pin = {
                    activity_id: 0,
                    coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
                    role: roleName
                } as Pin;

                let pin2 = {
                    activity_id: 1,
                    coordinates: {lat: 10, lon: 10} as LocationCoordinatesInterface,
                    role: roleName2
                } as Pin;

                let result = PinsController.getHighestRoleIndex([pin, pin2]);

                expect(result).toBe(correctResult);
            }
        )
    }
}

// --------- CONVERT FROM GOOGLE BOUNDS ---------- //
let testCases = [
    [1, 1, 0, 0],
    [30.45, 19.123, 30.34, 19.012],
    [123.456789, 89.123456, 123.123456, 89.0123456],
    [-1, -1, -2, -2]
];
for (let testCaseId in testCases) {
    let testCase = testCases[testCaseId];

    test('expect google bounds object to have same bounds when converted to our own bounds object', 
        () => {
            let northEast = {lat: () => { return testCase[0]; }, lng: () => { return testCase[1]; }};
            let southWest = {lat: () => { return testCase[2]; }, lng: () => { return testCase[3]; }};
            let googleBounds = {getNorthEast: () => { return northEast; }, getSouthWest: () => { return southWest }};

            let result = PinsController.convertFromGoogleBounds(googleBounds) as BoundingBoxInterface;

            expect(result.ne_lat).toBe(testCase[0]);
            expect(result.ne_lon).toBe(testCase[1]);
            expect(result.sw_lat).toBe(testCase[2]);
            expect(result.sw_lon).toBe(testCase[3]);
        }
    )
}

// --------- IS IN BOUNDS ---------- //
test('expect location within bounds to return true', 
    () => {
        let bounds = {ne_lat: 10, ne_lon: 10, sw_lat: -10, sw_lon: -10} as BoundingBoxInterface;
        let location = {lat: 0, lon: 0} as LocationCoordinatesInterface;

        let result = PinsController.isInBounds(bounds, location);

        expect(result).toBe(true);        
    }
)

test('expect location outside bounds to return false', 
    () => {
        let bounds = {ne_lat: 10, ne_lon: 10, sw_lat: -10, sw_lon: -10} as BoundingBoxInterface;
        let location = {lat: 11, lon: 11} as LocationCoordinatesInterface;

        let result = PinsController.isInBounds(bounds, location);

        expect(result).toBe(false);        
    }
)


let testCasesForInBounds = [
    {lat: 10, lon: 10} as LocationCoordinatesInterface,
    {lat: -10, lon: 10} as LocationCoordinatesInterface,
    {lat: 10, lon: -10} as LocationCoordinatesInterface,
    {lat: -10, lon: -10} as LocationCoordinatesInterface,
    {lat: 10, lon: 5} as LocationCoordinatesInterface,
    {lat: -10, lon: 5} as LocationCoordinatesInterface,
    {lat: 5, lon: -10} as LocationCoordinatesInterface,
    {lat: 5, lon: 10} as LocationCoordinatesInterface
];
for (let testCaseId in testCasesForInBounds) {
    let testCase = testCasesForInBounds[testCaseId];

    test('expect location on edge of bounds to return true', 
        () => {
            let bounds = {ne_lat: 10, ne_lon: 10, sw_lat: -10, sw_lon: -10} as BoundingBoxInterface;

            let result = PinsController.isInBounds(bounds, testCase);

            expect(result).toBe(true);        
        }
    )
}

// --------- CONVERT TO PINS ---------- //
test('expect activity converted to pin to have same location and activity id', 
    () => {
        let activity = {
            geoposition: {
                lat: 30,
                lon: 31
            } as LocationCoordinatesInterface,
            activity_id: 69
        } as CreateActivityRequest;

        let pins = PinsController.convertToPins([activity]);

        expect(pins).toHaveLength(1);   
        expect(pins[0].activity_id).toBe(69);
        expect(pins[0].coordinates.lat).toBe(30);
        expect(pins[0].coordinates.lon).toBe(31);
    }
)

test('expect multiple converted to pins to have same activity id', 
    () => {
        let activityArray = [];
        for (let i = 0; i < 5; i++) {
            let activity = {
                geoposition: {
                    lat: 30,
                    lon: 31
                } as LocationCoordinatesInterface,
                activity_id: 69 + i
            } as CreateActivityRequest;
            activityArray.push(activity);
        }

        let pins = PinsController.convertToPins(activityArray);

        expect(pins).toHaveLength(5);   
        expect(pins[0].activity_id).toBe(69);
        expect(pins[1].activity_id).toBe(70);
        expect(pins[2].activity_id).toBe(71);
        expect(pins[3].activity_id).toBe(72);
        expect(pins[4].activity_id).toBe(73);
    }
)

test('expect activity without pin to not be converted', 
    () => {
        let activity = {
            activity_id: 69
        } as CreateActivityRequest;

        let pins = PinsController.convertToPins([activity]);

        expect(pins).toHaveLength(0);
    }
)