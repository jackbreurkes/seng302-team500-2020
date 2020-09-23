import { LocationCoordinatesInterface } from './LocationCoordinatesInterface';

export interface Pin {
    activity_id: number
    coordinates: LocationCoordinatesInterface
    role: string | null
    isRecommended: boolean
}