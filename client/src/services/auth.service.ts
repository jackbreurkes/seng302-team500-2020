import instance from "./axios.service";

/**
 * @returns the currently logged in user's user id, or NaN if not found
 */
export function getMyUserId(): number | null {
  const profileId = parseInt(localStorage.getItem("userId") || "NaN");
  if (isNaN(profileId)) {
    return null;
  }
  return profileId;
}

/**
 * @returns the currently logged in user's token, or null if it does not exist
 */
export function getMyToken(): string | null {
  return localStorage.getItem("token");
}

/**
 * returns the currently logged in user's permission level, or 0 if not found
 */
function getMyPermissionLevel(): number {
  let level = parseInt(localStorage.getItem("permissionLevel") || "NaN");
  return isNaN(level) ? 0 : level; // default permission level is zero
}

/**
 * returns true if the current permission level is suitable for an admin, false otherwise.
 */
export function isAdmin(): boolean {
  return getMyPermissionLevel() >= 120;
}

/**
 * clears the stored token, user id and permission level information.
 */
export function clearAuthInfo() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("permissionLevel");
}

/**
 * Response format for POST /login
 * see API spec for more details
 */
interface LoginResponse {
  token: string;
  profile_id: string;
  permission_level: string;
}

/**
 * uses the whoami endpoint to check that the user's token matches their stored user id.
 * if the user's token and user id do not match, removes the stored auth info.
 * @returns true if the user's id matches their token, false otherwise
 */
export async function verifyUserId() {
  let userIdIsValid = false;

  if (getMyUserId() === null) {
    clearAuthInfo();
    return false;
  }

  try {
    let res = await instance.get("whoami");
    userIdIsValid = res.data.profile_id === getMyUserId();
  } catch (e) {
    if (!e.response) { // the server did not respond
      console.error(e)
      throw e; // don't clear auth info, instead throw the error
    }
  }

  if (!userIdIsValid) {
    clearAuthInfo();
  }
  return userIdIsValid;
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
  let res = await instance.post("login", { email, password });
  let responseData: LoginResponse = res.data;
  localStorage.setItem("token", responseData.token);
  localStorage.setItem("permissionLevel", responseData.permission_level);
  localStorage.setItem("userId", responseData.profile_id);
  return true;
}

/**
 * Logs out the currently logged in user and removes the stored auth info from the clientside.
 */
export async function logout() {
  await instance.delete("logmeout");
  clearAuthInfo();
}
