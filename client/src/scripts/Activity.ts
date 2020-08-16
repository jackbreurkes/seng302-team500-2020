export interface CreateActivityRequest {
    activity_id?: number;
    activity_name?: string;
    description?: string;
    activity_type?: string[];
    continuous?: boolean;
    start_time?: string;
    end_time?: string;
    location?: string;
    outcomes?: ActivityOutcomes[];
    num_followers?: number;
    num_participants?: number;
  }

export interface ActivityOutcomes {
  description: string;
  units: string;
}
  