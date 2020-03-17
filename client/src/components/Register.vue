<template>
  <div id="Register">

    

    <v-form submit="false">
      <v-container fluid>
        <v-col
        cols="12"
        sm="6"
        >
          <router-link to="/login">
           <p>Log In</p>
          </router-link>
          <p class="font-weight-bold">Blue fields are mandatory</p>
          <v-text-field label="lastname" ref="lastname" id="lastname" type="text" v-model="lastName" outlined=true rounded=true dense=true filled=true background-color=#c4daff></v-text-field>
          <v-text-field label="firstname" ref="firstname" id="firstname" type="text" v-model="firstName" outlined=true rounded=true dense=true filled=true background-color=#c4daff></v-text-field>
          <v-text-field label="middlename" ref="middlename" id="middlename" type="text" v-model="middleName" outlined=true rounded=true dense=true></v-text-field>
          <v-text-field label="nickname" ref="nickname" id="nickname" type="text" v-model="nickname" outlined=true rounded=true dense=true></v-text-field>
          <v-text-field label="email" ref="email" id="email" type="email" v-model="email" outlined=true rounded=true dense=true filled=true background-color=#c4daff></v-text-field>
          <v-text-field label="password" ref="password" id="password" type="password" v-model="password" outlined=true rounded=true dense=true filled=true background-color=#c4daff></v-text-field>
          <v-text-field label="confirm password" ref="confirm-password" id="confirm-password" type="password" v-model="confirmPassword" outlined=true rounded=true dense=true filled=true background-color=#c4daff></v-text-field>
          <v-text-field label="bio" ref="bio" id="bio" type="text" v-model="bio" outlined=true rounded=true dense=true></v-text-field>
          <v-text-field label="date of birth" ref="date-of-birth" id="date-of-birth" type="date" v-model="dateOfBirth" outlined=true rounded=true dense=true filled=true background-color=#c4daff></v-text-field>
          <label for="gender" class="font-weight-bold">Gender: </label>
          <select ref="gender" id="gender" v-model="gender">
            <option disabled value>Please select a gender...</option>
            <option value="male">Male</option>
            <option value="female">Female</option>
            <option value="non-binary">Non-binary</option>
          </select>
          <!-- <br> 2x just for aesthetic spacing -->
          <br>
          <br>
          <v-btn @click="saveButtonClicked" co>Save</v-btn>
          <br>
          <br>
          <p class="font-weight-bold">{{ errorMessage }}</p>
        </v-col>
      </v-container>
    </v-form>


    

    
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
