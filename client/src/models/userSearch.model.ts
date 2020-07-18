import { RegisterFormData } from "@/controllers/register.controller";
import { UserApiFormat } from "@/scripts/User";
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

import instance from "../services/axios.service";
import * as auth from "../services/auth.service";


export async function searchAppUsers(searchTerms: Dictionary<string>) {
    let res = await instance.get("profiles", { params: searchTerms });
    return res.data;
  }