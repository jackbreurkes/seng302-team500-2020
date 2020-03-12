import { RegisterFormData } from '@/controllers/register.controller';
import { UserApiFormat } from '@/scripts/User';


export async function login(email: string, password: string): Promise<boolean> {
  let res = await loginWithApi(email, password);
  if (res.status !== 201) {
    throw new Error(res.statusText);
  }
  const resData = await res.json();
  let token = resData.token;
  localStorage.setItem("token", token);
  return true;
}


async function loginWithApi(email: string, password: string): Promise<Response> {
  const data = {email, password}
  let res = await sendRequest("login",
    {
      credentials: "include",
      method: "POST",
      body: JSON.stringify(data)
    }, false)
  return res;
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
    })
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
