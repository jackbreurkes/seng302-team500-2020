import { LocationCoordinatesInterface } from './LocationCoordinatesInterface'

export interface CreateActivityRequest {
    activity_id?: number;
    activity_name?: string;
    description?: string;
    activity_type?: string[];
    continuous?: boolean;
    start_time?: string;
    end_time?: string;
    location?: string;
    geoposition?: LocationCoordinatesInterface;
    bounding_box?: LocationCoordinatesInterface[];  // array of size 2 - first element is southwest point, second is northeast point
    outcomes?: ActivityOutcomes[];
    num_followers?: number;
    num_participants?: number;
    creator_id?: number;
  }

export interface ActivityOutcomes {
  description: string;
  units: string;
  outcome_id?: number;
}

export interface ParticipantResult {
  score: string,
  date: string,
  time: string
}

export interface ParticipantResultDisplay {
  score: string;
  date: string;
  time: string;
  description?: string;
  units?: string;
}

