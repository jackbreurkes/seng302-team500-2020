import User, { UserBuilder } from '@/scripts/User';
import { getAll, create, login } from '@/models/user.model';

export interface RegisterFormData {
    firstName: string | null;
    lastName: string | null;
    middleName: string | null;
    nickname: string | null;
    email: string | null;
    password: string | null;
    confirmPassword: string | null;
    bio: string | null;
    dateOfBirth: string | null;
    gender: string | null;
}

export async function registerUser(formData: RegisterFormData) {
    let user: User = new UserBuilder()
    .setFirstName(formData.firstName)
    .setLastName(formData.lastName)
    .setMiddleName(formData.middleName)
    .setNickname(formData.nickname)
    .setEmail(formData.email)
    .setPassword(formData.password)
    .setBio(formData.bio)
    .setDateOfBirth(formData.dateOfBirth)
    .setGender(formData.gender)
    .build(); // will throw appropriate error if invalid data given
    
    if (formData.password !== formData.confirmPassword) {
      throw new Error("passwords do not match")
    }

    let users: User[] = await getAll();
    if (users.filter(user => user.primaryEmail === formData.email).length > 0) {
      throw new Error("user with that email already exists")
    }

    await create(user);
    await login(user.primaryEmail, user.password);
    return user;
  }