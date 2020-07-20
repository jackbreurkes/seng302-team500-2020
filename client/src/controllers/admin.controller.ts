import * as model from "../models/admin.model";

/**
 * authenticated as an admin, sets the given user to be an admin.
 * @param profileId the profile to set as an administrator
 */
export async function promoteUserToAdmin(profileId: number) {
    return await model.promoteProfile(profileId, "admin");
}

/**
 * authenticated as an admin, sets the given user to be a basic user.
 * @param profileId the profile to demote to basic user priveleges
 */
export async function demoteUserToBasicUser(profileId: number) {
    return await model.promoteProfile(profileId, "user");
}