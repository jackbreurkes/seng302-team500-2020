// import User, { UserBuilder } from '@/scripts/User';
import { create, getCurrentUser } from '@/models/user.model';
import { login } from "../services/auth.service";
import FormValidator from '@/scripts/FormValidator';
import { UserApiFormat } from '@/scripts/User';

let formValidator = new FormValidator();

export interface RegisterFormData {
    firstName?: string;
    lastName?: string;
    middleName?: string;
    nickname?: string;
    email?: string;
    password?: string;
    confirmPassword?: string;
    bio?: string;
    dateOfBirth?: string;
    gender?: string;
}

export async function registerUser(formData: RegisterFormData) {

    if (!formData.lastName || formData.lastName.length < 1) {
      throw new Error("No last name given")
    }

    if (formData.lastName.length > 30) {
        throw new Error("Last name must be less than 30 characters")
    }

    if (formValidator.hasNumber(formData.lastName)) {
        throw new Error("Last name cannot contain numbers")
    }

    if (!formData.firstName || formData.firstName.length < 1) {
        throw new Error("No first name given")
    }

    if (formData.firstName.length > 30) {
        throw new Error("First name must be less than 30 characters")
    }

    if (formValidator.hasNumber(formData.firstName)) {
        throw new Error("First name cannot contain numbers")
    }

    if (formData.middleName && formData.middleName.length > 30) {
        throw new Error("Middle name must be less than 30 characters")
    }

    if (formData.middleName && formValidator.hasNumber(formData.middleName)) {
        throw new Error("Middle name cannot contain numbers")
    }

    if (formData.nickname && formData.nickname.length < 6) {
        throw new Error("Nick name must be at least 6 characters long")
    }

    if (formData.nickname && formValidator.hasWhiteSpace(formData.nickname)) {
        throw new Error("Nickname cannot contain white space")
    }

    if (!formData.email || !formValidator.isValidEmail(formData.email)) {
        throw new Error("Invalid email address")
    }

    if (!formData.password) {
        throw new Error("Password cannot be empty")
    }

    if (formData.password !== formData.confirmPassword) {
        throw new Error("Passwords do not match")
    }

    if (formData.password.length < 8) {
        throw new Error("Password must be at least 8 characters")
    }

    if (formData.bio && formData.bio.length < 8) {
        throw new Error("Bio must be at least 8 characters")
    }

    if (!formData.dateOfBirth) {
        throw new Error("Date of birth cannot be empty")
    }
    // date of birth input format is YYYY-MM-DD
    const date = Date.parse(formData.dateOfBirth);
        if (isNaN(date)) {
        throw new Error('Valid date not given')
    }

    if (date > Date.now()) {
        throw new Error("Date of birth cannot be in the future")
    }

    if (!formData.gender) {
        throw new Error("No gender given")
    }

    await create(formData);
    await login(formData.email, formData.password);
    let user = await getCurrentUser();
    return user;
  }