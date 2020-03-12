import { RegisterFormData } from '@/controllers/register.controller';
import { UserApiFormat } from '@/scripts/User';


export async function login(email: string, password: string): Promise<UserApiFormat> {
  const users = await getAll();
  let targetUser = users.find(user => user.primary_email === email)

  if (targetUser === undefined) {
      throw new Error("no user with matching credentials found");
  }


  localStorage.currentUser = JSON.stringify(targetUser);
  return targetUser;
}


export async function logout() {
  localStorage.removeItem("currentUser")
}


export async function create(user: UserApiFormat) {
    let users = await getAll();
    users.push(user)
    localStorage.users = JSON.stringify(users);
}


export async function getCurrentUser() {
  if (!localStorage.currentUser) {
    return null;
  }
  let user: UserApiFormat = JSON.parse(localStorage.currentUser);
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
    users = JSON.parse(localStorage.users);
  } catch (err) {
    console.error(err);
    users = []
  }
  return users;
}
