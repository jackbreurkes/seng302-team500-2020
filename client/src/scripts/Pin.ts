import { LocationCoordinatesInterface } from './LocationCoordinatesInterface';

export interface Pin {
    activity_id: number
    coordinates: LocationCoordinatesInterface
    role: string | null
    is_recommended: boolean
}