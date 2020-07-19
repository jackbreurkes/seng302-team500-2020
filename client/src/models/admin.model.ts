import instance from "../services/axios.service";

/**
 * changes a user's role within the system.
 * @param profileId the profile whose role should be changed
 * @param roleName the role to change the profile to
 */
export async function promoteProfile(profileId: number, roleName: string) {
    let res = await instance.put(`profiles/${profileId}/role`, {role: roleName})
    return res.data;
}