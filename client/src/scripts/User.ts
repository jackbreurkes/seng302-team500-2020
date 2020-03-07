export interface UserInterface {
    firstName: string;
    lastName: string;
    middleName: string | null;
    nickname: string | null;
    primaryEmail: string;
    password: string;
    bio: string | null;
    dateOfBirth: string;
    gender: string;
    passports: string[];
    secondaryEmails: string[];
    fitnessLevel: number;
}

export class User implements UserInterface {
    firstName: string;
    lastName: string;
    middleName: string | null;
    nickname: string | null;
    primaryEmail: string;
    password: string;
    bio: string | null;
    dateOfBirth: string;
    gender: string;
    passports: string[];
    secondaryEmails: string[];
    fitnessLevel: number;

    constructor(build: UserBuilder) {

        if (!build.firstName || build.firstName.length < 1) {
            throw new Error("no first name given")
        }

        if (!build.lastName || build.lastName.length < 1) {
            throw new Error("no last name given")
        }

        if (!build.email || !this.validateEmail(build.email)) {
            throw new Error("invalid email address")
        }

        if (!build.password) {
            throw new Error("password cannot be empty")
        }

        if (!build.dateOfBirth) {
            throw new Error("date of birth cannot be empty")
        }
        // date of birth input format is YYYY-MM-DD
        const date = Date.parse(build.dateOfBirth);
            if (isNaN(date)) {
            throw new Error('valid date not given')
        }

        if (date > Date.now()) {
            throw new Error("date of birth cannot be in the future")
        }

        if (!build.gender) {
            throw new Error("no gender given")
        }

        this.firstName = build.firstName;
        this.lastName = build.lastName;
        this.middleName = build.middleName || null;
        this.nickname = build.nickname || null;
        this.primaryEmail = build.email;
        this.password = build.password;
        this.bio = build.bio || null;
        this.dateOfBirth = build.dateOfBirth;
        this.gender = build.gender;
        this.passports = [];
        this.secondaryEmails = [];
        this.fitnessLevel = 0
    }

    validateEmail(email: string) {
        // RegEx taken from https://emailregex.com/
        // eslint-disable-next-line no-useless-escape
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    addEmail(email: string) {
        if (!this.validateEmail(email)) {
            throw new Error("invalid email format");
        }
        this.secondaryEmails.push();
    }

}

export class UserBuilder {
    firstName: string | null = null;
    lastName: string | null = null;
    middleName: string | null = null;
    nickname: string | null = null;
    email: string | null = null;
    password: string | null = null;
    bio: string | null = null;
    dateOfBirth: string | null = null;
    gender: string | null = null;

    fromUserInterface(user: UserInterface) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.middleName = user.middleName;
        this.nickname = user.nickname;
        this.email = user.primaryEmail;
        this.password = user.password;
        this.bio = user.bio;
        this.dateOfBirth = user.dateOfBirth;
        this.gender = user.gender;
        return this;
    }
    setFirstName (firstName: string) {
        this.firstName = firstName;
        return this;
    }
    setLastName (lastName: string) {
        this.lastName = lastName;
        return this;
    }
    setMiddleName (middleName: string) {
        this.middleName = middleName;
        return this;
    }
    setNickname (nickname: string) {
        this.nickname = nickname;
        return this;
    }
    setEmail (email: string) {
        this.email = email;
        return this;
    }
    setPassword (password: string) {
        this.password = password;
        return this;
    }
    setBio (bio: string) {
        this.bio = bio;
        return this;
    }
    setDateOfBirth (dateOfBirth: string) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }
    setGender (gender: string) {
        this.gender = gender;
        return this;
    }
    build() {
        return new User(this);
    }
}
