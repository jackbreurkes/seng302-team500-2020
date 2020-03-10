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

export default class User implements UserInterface {
    firstName: string;
    lastName: string;
    middleName: string | null;
    nickname: string | null;
    primaryEmail: string;
    password: string;
    bio: string | null;
    dateOfBirth: string;
    gender: string;

    _passports: string[];
    set passports(passports: string[]) {
        this._passports = passports
    }
    get passports() {
        return this._passports
    }

    _secondaryEmails: string[];
    set secondaryEmails(emails: string[]) {
        emails.forEach(email => {
            if (!this._validateEmail(email)) {
                throw new Error(`${email} is not a valid email address`)
            }
        })
        this._secondaryEmails = emails;
    }
    get secondaryEmails(): string[] {
        return this._secondaryEmails
    }

    fitnessLevel: number;

    constructor(build: UserBuilder) {

        // swapped first name and last name around to follow order of the actual page
        if (!build.lastName || build.lastName.length < 1) {
            throw new Error("no last name given")
        }

        if (build.lastName.length > 30) {
            throw new Error("last name must be less than 30 characters")
        }

        if (this.hasNumber(build.lastName)) {
            throw new Error("last name cannot contain numbers")
        }

        if (!build.firstName || build.firstName.length < 1) {
            throw new Error("no first name given")
        }

        if (build.firstName.length > 30) {
            throw new Error("first name must be less than 30 characters")
        }

        if (this.hasNumber(build.firstName)) {
            throw new Error("first name cannot contain numbers")
        }

        if (build.middleName && build.middleName.length > 30) {
            throw new Error("middle name must be less than 30 characters")
        }

        if (build.middleName && this.hasNumber(build.middleName)) {
            throw new Error("middle name cannot contain numbers")
        }

        if (build.nickname && build.nickname.length < 6) {
            throw new Error("nick name must be at least 6 characters long")
        }

        if (build.nickname && this.hasWhiteSpace(build.nickname)) {
            throw new Error("nick name cannot contain white space")
        }

        if (!build.email || !this._validateEmail(build.email)) {
            throw new Error("invalid email address")
        }

        if (!build.password) {
            throw new Error("password cannot be empty")
        }

        if (build.password.length < 8) {
            throw new Error("password must be at least 8 characters")
        }

        if (build.bio && build.bio.length < 8) {
            throw new Error("Bio must be at least 8 characters")
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
        this._passports = [];
        this._secondaryEmails = [];
        this.fitnessLevel = 0
    }

    _validateEmail(email: string) {
        // RegEx taken from https://emailregex.com/
        // eslint-disable-next-line no-useless-escape
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    addEmail(email: string) {
        if (!this._validateEmail(email)) {
            throw new Error("invalid email format");
        }
        this.secondaryEmails.push(email);
    }

//  Function that checks if string "myString" contains a number
    hasNumber(myString: string) {
        return /\d/.test(myString);
      }

    hasWhiteSpace(myString: string) {
        return /\s/g.test(myString);
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
    setFirstName (firstName: string | null) {
        this.firstName = firstName;
        return this;
    }
    setLastName (lastName: string | null) {
        this.lastName = lastName;
        return this;
    }
    setMiddleName (middleName: string | null) {
        this.middleName = middleName;
        return this;
    }
    setNickname (nickname: string | null) {
        this.nickname = nickname;
        return this;
    }
    setEmail (email: string | null) {
        this.email = email;
        return this;
    }
    setPassword (password: string | null) {
        this.password = password;
        return this;
    }
    setBio (bio: string | null) {
        this.bio = bio;
        return this;
    }
    setDateOfBirth (dateOfBirth: string | null) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }
    setGender (gender: string | null) {
        this.gender = gender;
        return this;
    }
    build() {
        return new User(this);
    }
}
