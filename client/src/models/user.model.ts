import { RegisterFormData } from '@/controllers/register.controller';
import { UserApiFormat } from '@/scripts/User';


export async function login(email: string, password: string): Promise<boolean> {
  let res = await loginWithApi(email, password);
  const resData = await res.json();
  if (res.status !== 201) {
    console.error(resData);
    throw new Error(resData.message);
// export async function login(email: string, password: string): Promise<UserApiFormat> {
//   const users = await getAll();
//   let targetUser = users.find(user => user.primary_email === email)

//   if (targetUser === undefined) {
//       throw new Error("no user with matching credentials found");
  }
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
}


export async function create(user: UserApiFormat) {
    let users = await getAll();
    users.push(user)
    localStorage.users = JSON.stringify(users);
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


interface ViewProfileResponseFormat {
  user: {
    userId: number
  },
  fitness: number,
  lastName: string,
  firstName: string,
  middleName: string | null,
  nickName: string | null,
  bio: string | null,
  dob: number,
  gender: string
}


export async function getCurrentUser() {
  let res = await sendRequest("viewprofile", {
    method: "GET"
  });
  if (res.status !== 200) {
    throw new Error("failed to get current user")
  }
  let json: ViewProfileResponseFormat = await res.json();
  
  let user: UserApiFormat {
    lastname: json.lastName,
    firstname: json.firstName,
    primary_email: json.email,
    date_of_birth: "0000-01-01",
    gender: json.gender,
    fitness: 0,
    passports: [],
    additional_email: []
  }
  if (json.middleName) {
    user.middlename = json.middleName;
  }
  if (json.nickName) {
    user.nickname = json.nickName;
  }
  if (json.bio) {
    user.bio = json.bio;
  }

  // let user: UserApiFormat = JSON.parse(localStorage.currentUser);
  return user;

  // if (!localStorage.currentUser) {
  //   return null;
  // }
  // let userJson: any = JSON.parse(localStorage.currentUser);
  // let user: User = new UserBuilder().fromUserInterface(userJson).build();
  // user.passports = userJson._passports;
  // user.fitnessLevel = userJson.fitnessLevel;
  // user.secondaryEmails = userJson._secondaryEmails;
  // return user;
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
    parsedUsers = JSON.parse(localStorage.users);
    users = parsedUsers.map(userJson => {

      let user: User = new UserBuilder().fromUserInterface(userJson).build();
      user.passports = userJson._passports;
      user.fitnessLevel = userJson.fitnessLevel;
      user.secondaryEmails = userJson._secondaryEmails;
      return user

    })
    // users = JSON.parse(localStorage.users);
  } catch (err) {
    console.error(err);
    users = []
  }
  return users;
}
