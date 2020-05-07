export interface CreateActivityRequest {
    activity_name?: string;
    description?: string;
    activity_type?: string[];
    continuous?: boolean;
    start_time?: string;
    end_time?: string;
    location?: string;
  }
  