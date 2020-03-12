// import User, { UserBuilder } from '@/scripts/User';
import { getAll, create, login } from '@/models/user.model';
import { isValidEmail, hasNumber, hasWhiteSpace } from '@/scripts/LoginRegisterHelpers';
import { UserApiFormat } from '@/scripts/User';

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
      throw new Error("no last name given")
    }

    if (formData.lastName.length > 30) {
        throw new Error("last name must be less than 30 characters")
    }

    if (hasNumber(formData.lastName)) {
        throw new Error("last name cannot contain numbers")
    }

    if (!formData.firstName || formData.firstName.length < 1) {
        throw new Error("no first name given")
    }

    if (formData.firstName.length > 30) {
        throw new Error("first name must be less than 30 characters")
    }

    if (hasNumber(formData.firstName)) {
        throw new Error("first name cannot contain numbers")
    }

    if (formData.middleName && formData.middleName.length > 30) {
        throw new Error("middle name must be less than 30 characters")
    }

    if (formData.middleName && hasNumber(formData.middleName)) {
        throw new Error("middle name cannot contain numbers")
    }

    if (formData.nickname && formData.nickname.length < 6) {
        throw new Error("nick name must be at least 6 characters long")
    }

    if (formData.nickname && hasWhiteSpace(formData.nickname)) {
        throw new Error("nickname cannot contain white space")
    }

    if (!formData.email || !isValidEmail(formData.email)) {
        throw new Error("invalid email address")
    }

    if (!formData.password) {
        throw new Error("password cannot be empty")
    }

    if (formData.password.length < 8) {
        throw new Error("password must be at least 8 characters")
    }

    if (formData.bio && formData.bio.length < 8) {
        throw new Error("Bio must be at least 8 characters")
    }

    if (!formData.dateOfBirth) {
        throw new Error("date of birth cannot be empty")
    }
    // date of birth input format is YYYY-MM-DD
    const date = Date.parse(formData.dateOfBirth);
        if (isNaN(date)) {
        throw new Error('valid date not given')
    }

    if (date > Date.now()) {
        throw new Error("date of birth cannot be in the future")
    }

    if (!formData.gender) {
        throw new Error("no gender given")
    }
    
    if (formData.password !== formData.confirmPassword) {
      throw new Error("passwords do not match")
    }

    let users: UserApiFormat[] = await getAll();
    if (users.filter(user => user.primary_email === formData.email).length > 0) {
      throw new Error("user with that email already exists")
    }

    let userData: UserApiFormat = {
      lastname: formData.lastName,
      firstname: formData.firstName,
      primary_email: formData.email,
      date_of_birth: formData.dateOfBirth,
      gender: formData.gender,
      passports: [],
      fitness: 0,
      additional_email: [],
      password: formData.password
    }
    
    if (formData.middleName) {
      userData.middlename = formData.middleName;
    }

    if (formData.nickname) {
      userData.nickname = formData.nickname;
    }

    if (formData.bio) {
      userData.bio = formData.bio;
    }

    await create(userData);
    let user = await login(formData.email, formData.password);
    return user;
  }