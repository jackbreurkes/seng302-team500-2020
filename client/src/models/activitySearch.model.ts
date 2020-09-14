// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';
import instance from "../services/axios.service";


export async function searchAppActivities(termsToSend: string[]) {
    console.log(termsToSend)
    let res = await instance.get("activities", {params: {"searchTerms": termsToSend}});
    console.log(res.data);
    return res.data;
  }