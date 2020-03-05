<template>
  <div id="Register">
    <router-link to="/login">
      <p>this is a link to the login page</p>
    </router-link>

    <p>mandatory fields shown in blue</p>

    <form submit="false">
      <label for="lastname">lastname</label>
      <input ref="lastname" id="lastname" type="text" v-model="lastName">
      <label for="firstname">firstname</label>
      <input ref="firstname" id="firstname" type="text" v-model="firstName">
      <label for="middlename">middlename</label>
      <input ref="middlename" id="middlename" type="text" v-model="middleName">
      <label for="nickname">nickname</label>
      <input ref="nickname" id="nickname" type="text" v-model="nickname">
      <label for="email">email</label>
      <input ref="email" id="email" type="email" v-model="email">
      <label for="password">password</label>
      <input ref="password" id="password" type="password" v-model="password">
      <label for="password">confirm password</label>
      <input ref="confirm-password" id="confirm-password" type="password" v-model="confirmPassword">
      <label for="bio">bio</label>
      <input ref="bio" id="bio" type="text" v-model="bio">
      <label for="date-of-birth">date of birth</label>
      <input ref="date-of-birth" id="date-of-birth" type="date" v-model="dateOfBirth">

      <label for="gender">Gender:</label>
      <select ref="gender" id="gender" v-model="gender">
        <option disabled value="">Please select a gender...</option>
        <option value="male">Male</option>
        <option value="female">Female</option>
        <option value="non-binary">Non-binary</option>
      </select>

    </form>

    <button @click="submitForm">Save</button>
    
    <p>{{ errorMessage }}</p>
  </div>
</template>

<script>
  import { tagMandatoryAttributes } from '../scripts/login-register-helpers'
  import { UserBuilder } from '../scripts/User'

  // app Vue instance
  const Register = {
    name: 'Register',
    el: '#Register',
    
    // app initial state
    data: function() {
      return {
        mandatoryAttributes: [ 'lastname', 'firstname', 'email', 'date-of-birth', 'gender', 'password', 'confirm-password' ],
        lastName: "",
        firstName: "",
        middleName: "",
        nickname: "",
        email: "",
        password: "",
        confirmPassword: "",
        bio: "",
        dateOfBirth: "",
        gender: "",

        errorMessage: ""
      }
    },

    mounted: function() {
      let refs = this.$refs
      tagMandatoryAttributes(refs, this.mandatoryAttributes);
    },

    methods: {
      async registerUser() {

        let user = null;
        try {
          user = new UserBuilder()
            .setFirstName(this.firstName)
            .setLastName(this.lastName)
            .setMiddleName(this.middleName)
            .setNickname(this.nickname)
            .setEmail(this.email)
            .setPassword(this.password)
            .setBio(this.bio)
            .setDateOfBirth(this.dateOfBirth)
            .setGender(this.gender)
            .build();
        } catch (error) {
          console.error(error)
          this.errorMessage = error.message;
          return null;
        }

        if (this.password !== this.confirmPassword) {
          this.errorMessage = "passwords do not match"
          return null;
        }

        let users = [];
        if (localStorage.users !== undefined) {
          users = JSON.parse(localStorage.users)
        }

        if (users.filter(user => user.email === this.email).length > 0) {
          this.errorMessage = "user with that email already exists"
          return null
        }

        users.push(user);
        localStorage.users = JSON.stringify(users);
        localStorage.currentUser = JSON.stringify(user);
        return user;
      },

      async submitForm() {
        if (await this.registerUser() !== null) {
          this.$router.push({name: 'profilePage'})
        }
      }
    }
  }

  export default Register
</script>

<style>
  [v-cloak] { display: none; }

  .mandatory {
    background-color: #ccf;
  }
</style>
