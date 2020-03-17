import { RegisterFormData } from '@/controllers/register.controller';
import { UserApiFormat } from '@/scripts/User';
import axios from 'axios'  
  

/**
 * response format for POST /login
 * see API spec for more details
 */
interface LoginResponse {
  token: string,
  profile_id: string
}




const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({  
  baseURL: SERVER_URL,  
  timeout: 1000  
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
    throw new Error(e.response.data.error);
  }
  let responseData: LoginResponse = res.data.token;
  localStorage.setItem("token", responseData.token);
  localStorage.setItem("userId", res.data.profile_id);
  return true;
}

/**
 * Attempts to log the user into their account via GET /{{SERVER_URL}}/login
 * On unsuccessful login will throw an error containing the message from the endpoint.
 * On successful login will add the returned token and profile_id to localStorage.
 * For more endpoint information see file team-500/*.yaml
 * @param email the email to attempt a login with
 * @param password the user's password
 */
export async function getCurrentUser() {
  let res;
  try {
    res = await instance.get("profiles/" + localStorage.userId);
  } catch (e) {
    throw new Error(e.response.data.error);
  }
  let user: UserApiFormat = res.data;
  return user;
}


export async function logout() {
  localStorage.removeItem("currentUser")
  let res = await sendRequest("logmeout",
  {
    credentials: "include",
    method: "DELETE"
  })
  if (res.status !== 204) {
    throw new Error(res.statusText);
  }
}


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
    let res = await sendRequest("profiles",
    {
      credentials: "include",
      method: "POST",
      body: JSON.stringify(userData)
    }, false)
    console.log(res)
    let resData = await res.json();
    if (res.status !== 201) {
      throw new Error(resData);
    }
    return resData.profile_id;
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


export async function saveCurrentUser(user: UserApiFormat) {
  localStorage.currentUser = JSON.stringify(user)

  let users: Array<UserApiFormat> = JSON.parse(localStorage.users)
  for (let index = 0; index < users.length; index++) {
    if (users[index].primary_email === user.primary_email) {
      users.splice(index, 1, user);
    }
  }
  localStorage.users = JSON.stringify(users)
}


export async function getAll(): Promise<Array<UserApiFormat>> {
  return _getUsersFromLocalStorage();
}


function _getUsersFromLocalStorage(): Array<UserApiFormat> {
  let users: UserApiFormat[] = [];
  try {
    let users = JSON.parse(localStorage.users);
  } catch (err) {
    console.error(err);
    users = []
  }
  return users;
}
