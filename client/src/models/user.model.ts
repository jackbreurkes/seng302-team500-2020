import User, { UserBuilder, UserInterface } from '../scripts/User'


export async function login(email: string, password: string): Promise<boolean> {
  let res = await loginWithApi(email, password);
  if (res.status !== 201) {
    const resData = await res.json();
      console.error(resData);
      throw new Error(resData.message);
  }
  return true;
}


async function loginWithApi(email: string, password: string): Promise<Response> {
  const data = {email, password}
  let res = await fetch(process.env.VUE_APP_SERVER_ADD + process.env.VUE_APP_BASE_URL + "login",
    {
      credentials: "include",
      method: "POST",
      headers: {
         'Content-Type': 'application/json',
       },
      body: JSON.stringify(data)
    })
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


export async function getCurrentUser() {
  let res = await fetch(process.env.VUE_APP_SERVER_ADD + process.env.VUE_APP_BASE_URL + "viewprofile",
    {
      credentials: "include",
      method: "GET",
      headers: {
         'Content-Type': 'application/json'
       }
    })
  return res;

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
