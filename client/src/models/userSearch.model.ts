import { RegisterFormData } from "@/controllers/register.controller";
import { UserApiFormat } from "@/scripts/User";

import instance from "../services/axios.service";
import * as auth from "../services/auth.service";


export async function searchAppUsers(fullname: string, nickname: string, email: string) {
    let params = {"fullname": fullname, "nickname": nickname, "email": email}
    let res = await instance.get("profiles", { params });
    console.log(res.data)
    return res.data;
  }