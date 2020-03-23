import { RegisterFormData } from '@/controllers/register.controller';
import { UserApiFormat } from '@/scripts/User';
import axios from 'axios'


/**
 * Response format for POST /login
 * see API spec for more details
 */
interface LoginResponse {
  token: string,
  profile_id: string
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
    throw new Error("userId does not match")
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
    console.error(e.name);
    throw new Error(e.response.data.error);
  }
  let responseData: LoginResponse = res.data;
  localStorage.setItem("token", responseData.token);
  setMyUserId(responseData.profile_id);
  return true;
}

/**
 * Attempts to retrieve a user's info using their ID via GET /{{SERVER_URL}}/profiles/<their ID>
 * For more endpoint information see file team-500/*.yaml
 */
export async function getCurrentUser() {
  let res;
  try {
    res = await instance.get("profiles/" + getMyUserId());
  } catch (e) {
    console.error(e.response);
    throw new Error(e.response.data.error);
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
      firstname: formData.firstName,
      primary_email: formData.email,
      date_of_birth: formData.dateOfBirth,
      gender: formData.gender,
      passports: [],
      fitness: 0,
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
 * Gets the current user and then saves their profile information.
 * For more endpoint information see file team-500/*.yaml
 */
export async function saveCurrentUser() {
  const user = await getCurrentUser();
  saveUser(user);
}

export async function saveUser(user: UserApiFormat) {
  try {
    let notNullUser = user as any;
    for (let key in notNullUser) {
      if (notNullUser[key] === null) {
        delete notNullUser[key];
      }
    }
    await instance.put("profiles/" + localStorage.userId, notNullUser, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }
    });
  } catch (e) {
    throw new Error(e.response.data.error);
  }
}


export async function addEmail(email: string) {
  try {
    let user = await getCurrentUser();
    let emails = user.additional_email;
    if (emails === undefined) {
      emails = [email];
    } else {
      emails.push(email);
    }
    let emailDict = {"additional_email": emails, "email": email}
    console.log(JSON.stringify(emailDict));
    let res = await instance.post("profiles/" + localStorage.userId + "/emails", emailDict, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }, data: emailDict
    });
    console.log(JSON.stringify(res))
  } catch (e) {
    console.log(e)
    console.log(e.response.data)
    console.log(e.response.data.error)
    throw new Error(e.response.data.error)
  }
}


export async function updatePrimaryEmail(primaryEmail: string) {
  try {
    let user = await getCurrentUser();
    let emails = user.additional_email;
    let oldPrimaryEmail = user.primary_email;
    if (emails === undefined) {
      emails = [];
    }
    if (oldPrimaryEmail !== undefined) {
      emails.splice(emails.indexOf(primaryEmail), 1, oldPrimaryEmail);
    } else {
      emails.splice(emails.indexOf(primaryEmail), 1);
    }
    
    let emailDict = {"additional_email": emails, "primary_email": primaryEmail}
    console.log(JSON.stringify(emailDict));
    let res = await instance.put("profiles/" + localStorage.userId + "/emails", emailDict, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      },
      data: emailDict
    });
    console.log(JSON.stringify(res))
  } catch (e) {
    console.log(e)
    console.log(e.response.data)
    console.log(e.response.data.error)
    throw new Error(e.response.data.error)
  }
}

export async function deleteUserEmail(email: string) {
  try {
    let user = await getCurrentUser();
    let emails = user.additional_email;
    if (emails === undefined) {
      throw new Error("No additional emails to delete.");
    } else {
      let index = emails.indexOf(email);
      if (index == -1) {
        throw new Error("Email is not registered to user.");
      } else {
        emails.splice(index, 1);
      }
    }
    let emailDict = {"additional_email": emails}
    console.log(JSON.stringify(emailDict));
    let res = await instance.post("profiles/" + localStorage.userId + "/emails", emailDict, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }, data: emailDict
    });
    console.log(JSON.stringify(res))
  } catch (e) {
    console.log(e)
    console.log(e.response.data)
    console.log(e.response.data.error)
    throw new Error(e.response.data.error)
  }
}


export async function updateCurrentPassword(old_password: string, new_password: string, repeat_password: string) {
  let res;
  try {
    res = await instance.post("profiles/" + getMyUserId() + "/password",
    {old_password, new_password, repeat_password},
    {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }
    });
  } catch (e) {
    throw new Error(e.response.data.error);
  }
}
