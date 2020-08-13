import { UpdateUserActivityRoleRequest } from "../scripts/ActivityRole";
import instance from "@/services/axios.service";

/**
 * Sets the role of the user for a specific activity
 * @param data The request data containing which role to change to
 * @param profileId the id of the profile to be toggled
 * @param activityId The id of the activity to change the role for
 */
export async function setRole(data: UpdateUserActivityRoleRequest, profileId: number,
                              activityId: number) {
    let res = await instance.put(`activities/${activityId}/roles/${profileId}`)
    return res.data;
}