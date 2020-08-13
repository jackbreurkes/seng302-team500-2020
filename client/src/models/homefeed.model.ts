import instance from "../services/axios.service";
import * as auth from "../services/auth.service";

/**
 * Retriveds homefeed data from backend
 * homefeed/profileid
 */
 export async function getUsersHomefeed(){
     let res = await instance.get("homefeed/" + auth.getMyUserId());
     return res.data as JSON[];
 }