import User, { UserBuilder, UserInterface } from '../scripts/User'


export async function login(email: string, password: string): Promise<User> {
  const users = await getAll();
  let targetUser = users.find(user => user.primaryEmail === email && user.password === password)

  if (targetUser === undefined) {
      throw new Error("no user with matching credentials found");
  }

  localStorage.currentUser = JSON.stringify(targetUser);
  return targetUser;
}


export async function create(user: User) {
    let users = await getAll();
    users.push(user);
    localStorage.users = JSON.stringify(users);
}


export async function getAll(): Promise<Array<User>> {
  return _getUsersFromLocalStorage();
}


function _getUsersFromLocalStorage(): Array<User> {
  let parsedUsers: UserInterface[] = [];
  let users: User[] = [];
  try {
    parsedUsers = JSON.parse(localStorage.users);
    users = parsedUsers.map(user => {
        let newUser = new UserBuilder().fromUserInterface(user).build()
        newUser.passports = user.passports
        newUser.fitnessLevel = user.fitnessLevel
        return newUser
    })
  } catch (err) {
    console.error(err);
    users = []
  }
  return users;
}
