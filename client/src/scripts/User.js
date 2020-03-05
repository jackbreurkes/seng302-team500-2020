export class User {

    constructor(build) {

        if (!build.firstName?.length > 0) {
            throw new Error("no first name given")
        }

        if (!build.lastName?.length > 0) {
            throw new Error("no last name given")
        }

        if (!this.validateEmail(build.email)) {
            throw new Error("invalid email address")
        }

        if (!build.password) {
            throw new Error("password cannot be empty")
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
    }

    validateEmail(email) {
        // RegEx taken from https://emailregex.com/
        // eslint-disable-next-line no-useless-escape
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    addEmail(email) {
        if (!this.validateEmail(email)) {
            throw new Error("invalid email format");
        }
        this.secondaryEmails.push();
    }

}

export class UserBuilder {

    setFirstName (firstName) {
        this.firstName = firstName;
        return this;
    }
    setLastName (lastName) {
        this.lastName = lastName;
        return this;
    }
    setMiddleName (middleName) {
        this.middleName = middleName;
        return this;
    }
    setNickname (nickname) {
        this.nickname = nickname;
        return this;
    }
    setEmail (email) {
        this.email = email;
        return this;
    }
    setPassword (password) {
        this.password = password;
        return this;
    }
    setBio (bio) {
        this.bio = bio;
        return this;
    }
    setDateOfBirth (dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }
    setGender (gender) {
        this.gender = gender;
        return this;
    }
    build() {
        return new User(this);
    }
}
