import { login, getCurrentUser } from '../models/user.model'

export interface LoginFormData {
    email: string;
    password: string;
}

export async function submitForm(formData: LoginFormData) {
    let loggedIn = await login(formData.email, formData.password);
    // console.log(loggedIn);
    // let user = await getCurrentUser();
    return
}
