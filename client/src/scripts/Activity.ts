export interface CreateActivityRequest {
    activity_id?: number;
    activity_name?: string;
    description?: string;
    activity_type?: string[];
    continuous?: boolean;
    start_time?: string;
    end_time?: string;
    location?: string;
    geoposition?: GeoPosition;
    bounding_box?: GeoPosition[];
    outcomes?: ActivityOutcomes[];
    num_followers?: number;
    num_participants?: number;
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

export interface GeoPosition {
  lat: number;
  lon: number;
}
