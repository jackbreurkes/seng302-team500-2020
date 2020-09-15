import { LocationCoordinatesInterface } from './LocationCoordinatesInterface';

export interface Pin {
    activity_id: number
    location: LocationCoordinatesInterface
    role?: string
}