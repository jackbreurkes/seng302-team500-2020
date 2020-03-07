export function tagMandatoryAttributes(refs: { [x: string]: any; }, attributes: any) {
    for (let attribute of attributes) {
      let field = refs[attribute];
      field.setAttribute("class", "mandatory");
      field.required = true;
    }
  }

export async function login(email: string, password: string) {
  const users = await _getUsers();
  let targetUser = null;
  for (let user of users) {
    if (user.primaryEmail === email && user.password === password) {
      targetUser = user;
      break;
    }
  }

  if (targetUser !== null) {
    localStorage.currentUser = JSON.stringify(targetUser);
  }

  return targetUser;
}

async function _getUsers() {
  return _getUsersFromLocalStorage();
}

function _getUsersFromLocalStorage() {
  let users = []
  try {
    users = JSON.parse(localStorage.users);
  } catch (err) {
    console.error(err);
  }
  return users;
}