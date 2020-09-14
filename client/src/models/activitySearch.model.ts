// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';
import instance from "../services/axios.service";


export async function searchAppActivities(searchTerms: Dictionary<string>) {
    let res = await instance.get("activities", { params: searchTerms });
    return res.data;
  }