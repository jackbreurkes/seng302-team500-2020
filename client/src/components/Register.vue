<template>
  <div id="Register">
    <router-link to="/login">
      <p>this is a link to the login page</p>
    </router-link>

    <p>mandatory fields shown in blue</p>

    <form submit="false">
      <label for="lastname">lastname</label>
      <input ref="lastname" id="lastname" type="text" v-model="lastName" />
      <label for="firstname">firstname</label>
      <input ref="firstname" id="firstname" type="text" v-model="firstName" />
      <label for="middlename">middlename</label>
      <input ref="middlename" id="middlename" type="text" v-model="middleName" />
      <label for="nickname">nickname</label>
      <input ref="nickname" id="nickname" type="text" v-model="nickname" />
      <label for="email">email</label>
      <input ref="email" id="email" type="email" v-model="email" />
      <label for="password">password</label>
      <input ref="password" id="password" type="password" v-model="password" />
      <label for="password">confirm password</label>
      <input ref="confirm-password" id="confirm-password" type="password" v-model="confirmPassword" />
      <label for="bio">bio</label>
      <input ref="bio" id="bio" type="text" v-model="bio" />
      <label for="date-of-birth">date of birth</label>
      <input ref="date-of-birth" id="date-of-birth" type="date" v-model="dateOfBirth" />

      <label for="gender">Gender:</label>
      <select ref="gender" id="gender" v-model="gender">
        <option disabled value>Please select a gender...</option>
        <option value="male">Male</option>
        <option value="female">Female</option>
        <option value="non-binary">Non-binary</option>
      </select>
    </form>

    <button @click="saveButtonClicked">Save</button>

    <p>{{ errorMessage }}</p>
  </div>
</template>

<script lang="ts">
import { tagMandatoryAttributes } from "../scripts/LoginRegisterHelpers";
// eslint-disable-next-line no-unused-vars
import { registerUser, RegisterFormData } from "../controllers/register.controller";
import Vue from 'vue'

// app Vue instance
const Register = Vue.extend({
  name: "Register",
  el: "#Register",

  // app initial state
  data: function() {
    return {
      mandatoryAttributes: [
        "lastname",
        "firstname",
        "email",
        "date-of-birth",
        "gender",
        "password",
        "confirm-password"
      ],
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
    };
  },

  mounted: function() {
    let refs = this.$refs;
    tagMandatoryAttributes(refs, this.mandatoryAttributes);
  },

  methods: {
    saveButtonClicked() {
      let formData: RegisterFormData = {
        firstName: this.firstName,
        lastName: this.lastName,
        middleName: this.middleName,
        nickname: this.nickname,
        email: this.email,
        password: this.password,
        confirmPassword: this.confirmPassword,
        bio: this.bio,
        dateOfBirth: this.dateOfBirth,
        gender: this.gender
      }
      registerUser(formData)
        .then(() => {
          this.$router.push({ name: "profilePage" });
        })
        .catch(err => {
          this.errorMessage = err.message;
        });
      this.errorMessage = "submitting...";
    }
  }
});

export default Register;
</script>

<style>
[v-cloak] {
  display: none;
}

.mandatory {
  background-color: #ccf;
}
</style>
