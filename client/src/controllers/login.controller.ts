import { login, getCurrentUser } from '../models/user.model'

export interface LoginFormData {
    email: string;
    password: string;
}

export async function submitForm(formData: LoginFormData) {
    await login(formData.email, formData.password);
}
