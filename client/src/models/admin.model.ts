import instance from "../services/axios.service";

export async function promoteProfile(profileId: number, roleName: string) {
    let res = await instance.put(`profiles/${profileId}/role`, {role: roleName})
    return res.data;
}