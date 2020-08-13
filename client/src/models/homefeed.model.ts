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