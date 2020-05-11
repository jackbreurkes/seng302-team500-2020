import { CreateActivityRequest } from "../scripts/Activity";
import axios from "axios";
const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

axios.interceptors.request.use(function(config) {
  const token = localStorage.getItem("token") || "";
  config.headers["X-Auth-Token"] = token;
  return config;
});

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 10000,
});

/**
 * takes an error thrown by axios and returns a reader-friendly message.
 * @param e the error thrown by axios
 * @returns a reader-friendly message explaining the error
 */
function axiosErrorToMessage(e: any) {
  if (e.response) {
    // request made and server responded
    return e.response.data.error;
  } else if (e.request) {
    return "unable to complete request";
  } else {
    // something happened in setting up the request
    return "unknown error - check console for more info";
  }
}

export async function createActivity(
  data: CreateActivityRequest,
  profileId: number
) {
  let res;
  try {
    res = await instance.post(`/profiles/${profileId}/activities`, data, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      },
    });
  } catch (e) {
    if (e.response) {
      // request made and server responded
      console.error(e.response);
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else {
      // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  // TODO handle response
}

export async function editActivity(data: CreateActivityRequest, profileId: number,
  activityId: number) {
  let res;
  try {
    res = await instance.put(`/profiles/${profileId}/activities/${activityId}`, data, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      },
    });
  } catch (e) {
    if (e.response) {
      // request made and server responded
      console.error(e.response);
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else {
      // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  // TODO handle response
}

export async function loadAvailableActivityTypes(): Promise<string[]> {
  let res;
  try {
    res = await instance.get("/activity-types");
  } catch (e) {
    if (e.response) {
      // request made and server responded
      console.error(e.response);
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else {
      // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  return res.data;
}


export async function getActivitiesByCreator(creatorId: number): Promise<CreateActivityRequest[]> {
  let res;
  try {
    res = await instance.get(`/profiles/${creatorId}/activities`, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      },
    });
  } catch (e) {
    if (e.response) {
      // request made and server responded
      console.error(e.response);
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else {
      // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  return res.data;
}

/**
 * Gets an activity by a user's ID and the activity ID
 * 
 * @param {number} creatorId User the activity belongs to
 * @param {number} activityId Activity ID
 * @return {CreateActivityRequest} Retrieved activity data
 */
export async function getActivityById(creatorId: number, activityId: number): Promise<CreateActivityRequest> {
  let res;
  try {
    res = await instance.get(`/profiles/${creatorId}/activities/${activityId}`, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      },
    });
  } catch (e) {
    if (e.response) {
      // request made and server responded
      console.error(e.response);
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else {
      // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  return res.data;
}


/**
 * Deletes an activity by a user's ID and the activity ID
 * 
 * @param {number} creatorId User the activity belongs to
 * @param {number} activityId Activity ID
 * @return {CreateActivityRequest} Retrieved activity data
 */
export async function deleteActivityById(creatorId: number, activityId: number) {
  let res;
  try {
    res = await instance.delete(`/profiles/${creatorId}/activities/${activityId}`, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      },
    });
  } catch (e) {
    console.log(e)
    throw axiosErrorToMessage(e);
  }
}
