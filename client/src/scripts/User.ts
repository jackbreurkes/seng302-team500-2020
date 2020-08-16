import { LocationInterface } from './LocationInteface';

export interface UserApiFormat {
    profile_id?: number,
    permission_level?: number,
    lastname?: string,
    firstname?: string,
    middlename?: string,
    nickname?: string,
    primary_email?: string,
    bio?: string,
    date_of_birth?: string,
    gender?: string,
    fitness?: number,
    passports?: string[],
    additional_email?: string[],
    activities?: string[],
    password?: string,
    location?: LocationInterface,
    activityRole?: string
}