import { login } from '../services/auth.service';

export interface LoginFormData {
    email: string;
    password: string;
}

export async function submitForm(formData: LoginFormData) {
    await login(formData.email, formData.password);
}
