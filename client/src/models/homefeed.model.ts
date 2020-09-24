import instance from "../services/axios.service";
import * as auth from "../services/auth.service";
import { HomeFeedCardType } from '@/scripts/HomeFeedCardType';

/**
 * Retrieves data from "/homefeed/{{profileId}}" endpoint
 */
 export async function getUsersHomefeed(){
     let res = await instance.get("homefeed/" + auth.getMyUserId());
     return res.data as HomeFeedCardType[];
 }

 /**
 * Retrieves suggested activities from "/homefeed/{{profileId}}/recommnedations" endpoint
 */
export async function getSuggestedActivities(){
    let res = await instance.get("homefeed/" + auth.getMyUserId() + "/recommendations");
    return res.data as HomeFeedCardType[];
}

 /**
 * Retrieves data from "/homefeed/{{profileId}}" endpoint after a given changelog id
 * @param paginationId the id of the last loaded homefeed changelog entry
 */
export async function getAdditionalUsersHomefeed(paginationId: number): Promise<HomeFeedCardType[]> {
    let res = await instance.get("homefeed/" + auth.getMyUserId(), {
        params: {
            lastId: paginationId
        }
    });
    return res.data;
}