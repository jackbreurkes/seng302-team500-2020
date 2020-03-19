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
  localStorage.setItem("userId", responseData.profile_id);
  return true;
}

/**
 * Attempts to retrieve a user's info using their ID via GET /{{SERVER_URL}}/login
 * For more endpoint information see file team-500/*.yaml
 */
export async function getCurrentUser() {
  let res;
  try {
    res = await instance.get("profiles/" + localStorage.userId);
  } catch (e) {
    console.error(e.response);
    throw new Error(e.response.data.error);
  }
  let user: UserApiFormat = res.data;
  return user;
}


export async function logout() {
  let res = await sendRequest("logmeout",
  {
    credentials: "include",
    method: "DELETE"
  })
  if (res.status !== 204) {
    throw new Error(res.statusText);
  }
  localStorage.removeItem("token")
  localStorage.removeItem("userId")
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


async function sendRequest(endpoint: string, options: RequestInit, authenticated: boolean = true) {
  if (!options.headers) {
    options.headers = {};
  }
  options.headers = {...options.headers, "Content-Type": "application/json"};
  if (authenticated) {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("no token found")
    }
    options.headers = {...options.headers, "X-Auth-Token": token};
  }

  let res = await fetch(process.env.VUE_APP_SERVER_ADD + process.env.VUE_APP_BASE_URL + endpoint,
    options)
  return res;
}


export async function getCurrentUser() {
  let res = await sendRequest("profiles", {
    credentials: 'include',
    method: "GET"
  });
  if (res.status !== 200) {
    throw new Error("failed to get current user")
  }
  let user: UserApiFormat = await res.json();
  return user;
}


async function saveUser(user: UserApiFormat) {
  /*localStorage.currentUser = JSON.stringify(user)

  let users: Array<UserApiFormat> = JSON.parse(localStorage.users)
  for (let index = 0; index < users.length; index++) {
    if (users[index].primary_email === user.primary_email) {
      users.splice(index, 1, user);
    }
  }
  localStorage.users = JSON.stringify(users)*/

  let res = await sendRequest('/profiles/' + user.profile_id, {credentials: 'include', method: "PUT", body: JSON.stringify(user)}, true);
  if (res.status != 200) {
    throw new Error("Failed to save user.");
  }
}

export async function saveCurrentUser() {
  const user = await getCurrentUser();
  saveUser(user);
}

export async function getAll(): Promise<Array<UserApiFormat>> {
  return _getUsersFromLocalStorage();
}


function _getUsersFromLocalStorage(): Array<UserApiFormat> {
  let users: UserApiFormat[] = [];
export async function saveCurrentUser(user: UserApiFormat) {
  let res;
  try {
    res = await instance.post("profiles", user);
  } catch (e) {
    throw new Error(e.response.data.error);
  }
}