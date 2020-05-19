import instance from "./axios.service";

/**
 * @returns the currently logged in user's user id, or null if not found
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
export function getMyPermissionLevel(): number {
  let level = parseInt(localStorage.getItem("permissionLevel") || "NaN");
  return isNaN(level) ? 0 : level; // default permission level is zero
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
 * uses the whoami endpoint to check that the user's token matches their stored user id
 */
export async function verifyUserId() {
  let res;
  res = await instance.get("whoami");
  if (res.data.profile_id == getMyUserId() || res.data.profile_id == -1) {
    return true;
  } else {
    throw "userId in localStorage does not match";
    clearAuthInfo();
  }
}

function setMyUserId(value: string | null) {
  if (value === null) {
    localStorage.removeItem("userId");
  } else {
    localStorage.setItem("userId", value);
  }
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
