import User, { UserBuilder, UserInterface } from '../scripts/User'


export async function login(email: string, password: string): Promise<boolean> {
  let res = await loginWithApi(email, password);
  const resData = await res.json();
  if (res.status !== 201) {
    console.error(resData);
    throw new Error(resData.message);
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


export async function create(user: User) {
    let users = await getAll();
    users.push(user);
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
  
  let user: User = new UserBuilder()
                .setFirstName(json.firstName)
                .setLastName(json.lastName)
                .setMiddleName(json.middleName)
                .setNickname(json.nickName)
                .setBio(json.bio)
                .setDateOfBirth("2000-01-01")
                .setGender(json.gender)
                .setEmail("temorary@bugfix.com")
                .setPassword("mysecretpassword")
                .build()
  user.fitnessLevel = json.fitness;

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


export async function saveCurrentUser(user: User) {
  localStorage.currentUser = JSON.stringify(user)

  let users: Array<User> = JSON.parse(localStorage.users)
  for (let index = 0; index < users.length; index++) {
    if (users[index].primaryEmail === user.primaryEmail) {
      users.splice(index, 1, user);
    }
  }
  localStorage.users = JSON.stringify(users)
}


export async function getAll(): Promise<Array<User>> {
  return _getUsersFromLocalStorage();
}


function _getUsersFromLocalStorage(): Array<User> {
  let parsedUsers: any[] = [];
  let users: User[] = [];
  try {
    parsedUsers = JSON.parse(localStorage.users);
    users = parsedUsers.map(userJson => {

      let user: User = new UserBuilder().fromUserInterface(userJson).build();
      user.passports = userJson._passports;
      user.fitnessLevel = userJson.fitnessLevel;
      user.secondaryEmails = userJson._secondaryEmails;
      return user

    })
  } catch (err) {
    console.error(err);
    users = []
  }
  return users;
}
