import { login, getCurrentUser } from '../models/user.model'

export interface LoginFormData {
    email: string;
    password: string;
}

export async function submitForm(formData: LoginFormData) {
    let loggedIn = await login(formData.email, formData.password);
    let userInfo = await getCurrentUser();
    console.log(userInfo);
    // let userInfo = await login(formData.email, formData.password);

    // if (userInfo === null) {
        // throw new Error("no matching user found");
    // } else {
        // return
    // }
}
