export interface UserApiFormat {
    profile_id?: number,
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
    password?: string
}