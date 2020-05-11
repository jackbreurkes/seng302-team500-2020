import { RegisterFormData } from '@/controllers/register.controller';
import { UserApiFormat } from '@/scripts/User';
import axios from 'axios'


/**
 * Response format for POST /login
 * see API spec for more details
 */
interface LoginResponse {
  token: string,
  profile_id: string,
  permission_level: string
}




const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

axios.interceptors.request.use(function (config) {
  const token = localStorage.getItem("token") || "";
  config.headers["X-Auth-Token"] = token;
  return config;
})

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 10000
});

export function getMyPermissionLevel(): number {
  let level = localStorage.getItem("permissionLevel");
  if (!level) {
    return 0;
  }
  return parseInt(level);
}

function getMyUserId() {
  return localStorage.getItem("userId")
}

export async function verifyUserId() {
  let res;
  try {
    res = await instance.get("whoami", {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }
    });
  } catch (e) {
    console.error(e.name);
    throw new Error(e.response.data.error);
  }
  if (res.data.profile_id == localStorage.getItem("userId")
    || res.data.profile_id == -1) {
      return true;
  } else {
    throw new Error("userId in localStorage does not match")
  }
}

function setMyUserId(value: string | null) {
  if (value === null) {
    localStorage.removeItem("userId")
  } else {
    localStorage.setItem("userId", value);
  }
}


/**
 * Attempts to log the user into their account via POST /{{SERVER_URL}}/login
 * On unsuccessful login will throw an error containing the message from the endpoint.
 * On successful login will add the returned token and profile_id to localStorage.
 * For more endpoint information see file team-500/*.yaml
 * @param email the email to attempt a login with
 * @param password the user's password
 */
export async function login(email: string, password: string): Promise<boolean> {
  let res;
  try {
    res = await instance.post("login", {email, password});
  } catch (e) {
    if (e.response) { // request made and server responded
      console.error(e.response)
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else { // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  let responseData: LoginResponse = res.data;
  localStorage.setItem("token", responseData.token);
  localStorage.setItem("permissionLevel", responseData.permission_level);
  setMyUserId(responseData.profile_id);
  return true;
}

/**
 * Attempts to retrieve a user's info using the saved ID via GET /{{SERVER_URL}}/profiles/<their ID>
 * For more endpoint information see file team-500/*.yaml
 */
export async function getCurrentUser() {
  let res;
  try {
    res = await instance.get("profiles/" + getMyUserId(), {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      }
    });
  } catch (e) {
    console.error(e.response);
    throw new Error(e.response.data.error);
  }
  let user: UserApiFormat = res.data;
  return user;
}

/**
 * Attempts to retrieve a profile's info using the given ID via GET /{{SERVER_URL}}/profiles/<their ID>
 * For more endpoint information see file team-500/*.yaml
 */
export async function getProfileById(profileId: number) {
  let res;
  try {
    res = await instance.get("profiles/" + profileId, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token"),
      }
    });
  } catch (e) {
    if (e.response) { // request made and server responded
      console.error(e.response)
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else { // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  let user: UserApiFormat = res.data;
  return user;
}


/**
 * Logs out the currently logged in user.
 * For more endpoint information see file team-500/*.yaml
 */
export async function logout() {
  try {
    await instance.delete("logmeout", {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }
    });
  } catch (e) {
    console.error(e.response);
    throw new Error(e.response.data.error);
  }
  localStorage.removeItem("token"); //still remove token if not deleted from backend
  localStorage.removeItem("permissionLevel"); //remove our permission level too
  setMyUserId(null); // removes userId
}

/**
 * creates a user given the data from the register form. Returns the new user's profile ID.
 * For more endpoint information see file team-500/*.yaml
 * @param formData the register form data to use to create the user
 */
export async function create(formData: RegisterFormData) {
    let userData = {
      lastname: formData.lastName,
      middlename: formData.middleName,
      firstname: formData.firstName,
      nickname: formData.nickname,
      primary_email: formData.email,
      date_of_birth: formData.dateOfBirth,
      bio: formData.bio,
      gender: formData.gender,
      passports: [],
      fitness: -1,
      additional_email: [],
      password: formData.password
    }
    let res;
    try {
      res = await instance.post("profiles", userData, {
        headers: {
          "X-Auth-Token": localStorage.getItem("token")
        }
      });
    } catch (e) {
      throw new Error(e.response.data.error);
    }
    let user: UserApiFormat = res.data;
    return user.profile_id;
}

/**
 * Takes a user and then saves their profile information under the user id in local storage.
 * For more endpoint information see file team-500/*.yaml
 */
export async function saveUser(user: UserApiFormat, profileId: number) {
  console.log("saving user")
  if (profileId !== user.profile_id) {
    throw new Error("cannot save a profile to an id different from that which appears in the user object")
  }
  try {
    let notNullUser = user as any;
    for (let key in notNullUser) {
      if (notNullUser[key] === null) {
        delete notNullUser[key];
      }
    }
    console.log(JSON.stringify(notNullUser))
    let res = await instance.put("profiles/" + profileId, notNullUser, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }
    });
  } catch (e) {
    if (e.response) { // request made and server responded
      console.error(e.response)
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else { // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
}

/**
 * Register the specified email to the target profile by adding it to the list of additional emails.
 * @param email email to register to user
 */
export async function updateActivityTypes(selectedActivities: string[], profileId: any) {
  try {
    let activityDict = {"activities": selectedActivities}
    let res = await instance.put("profiles/" + profileId + "/activity-types", activityDict, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }//, data: activityDict
    });
  } catch (e) {
    throw new Error(e.response.data.error)
    
  }
}



/**
 * Register the specified email to the target profile by adding it to the list of additional emails.
 * @param email email to register to user
 */
export async function updateEmailList(newEmails: string[], profileId: number) {
  try { // TODO there should be no business logic in the model class
    let user = await getProfileById(profileId);
    let emails = user.additional_email;
    if (emails === undefined) {
      emails = newEmails;
    } else {
      emails = newEmails;
    }
    let emailDict = {"additional_email": emails}
    let res = await instance.post("profiles/" + profileId + "/emails", emailDict, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }, data: emailDict
    });
  } catch (e) {
    throw new Error(e.response.data.error)
  }
}

/**
 * update the user's emails by sending a request to the back end
 * @param primaryEmail the new primary email for the user
 * @param additionalEmails the list of secondary emails for the user
 * @param profileId the profile id of the user whose emails should be updated
 */
export async function updateEmails(primaryEmail: string, additionalEmails: string[], profileId: number) {
  try {

    let res = await instance.put("profiles/" + profileId + "/emails", null, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }, data: {"additional_email": additionalEmails, "primary_email": primaryEmail}
    });
  } catch (e) {
    throw new Error(e.response.data.error)
  }

}

/**
 * Saves the current user's activity types list using PUT /profiles/{profileId}/activity-types endpoint
 * @param user UserApiFormat user with already-updated (in controller class) list of activities
 */
export async function saveActivityTypes(user: UserApiFormat, profileId: number) {
  try {
    let body = {"activities": user.activities}
    // PUT /profiles/{profileId}/activity-types
    let res = await instance.put(`profiles/${profileId}/activity-types`, body, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }
    });
  } catch (e) {
    if (e.response) { // request made and server responded
      console.error(e.response)
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else { // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
}

export async function updateCurrentPassword(old_password: string, new_password: string, repeat_password: string, profileId: number) {
  let res;
  try {
    res = await instance.put("profiles/" + profileId + "/password",
    {old_password, new_password, repeat_password},
    {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }

    },

    
    );
  } catch (e) {
    throw new Error(e.response.data.error);
  }
}
