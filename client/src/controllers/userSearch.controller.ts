import { searchAppUsers } from '../models/userSearch.model';

export async function searchUsers(fullname: string, nickname: string, email: string) {
    return await searchAppUsers(fullname, nickname, email);
  }
