<template>
  <div id="Register">

    

    <v-form submit="false">
      <v-container class="fill-height" fluid>
        <v-row
        align="center"
        justify="center"
        >
          <v-col
          cols="12"
          sm="8"
          md="4"
          >
            <v-card class="elevation-12" width=100%>
              <v-toolbar
                color="primary"
                dark
                flat
              >
              <v-toolbar-title>Register:</v-toolbar-title>   
              </v-toolbar>
              <v-card-text>
                <p>Already registered?</p>
                <router-link to="/login">
                  <p class="pl-1">Log In</p>
                </router-link>
                <br>
                <p class="text--disabled">*Blue fields are mandatory</p>
                <v-text-field label="lastname" ref="lastname" id="lastname" type="text" v-model="lastName" background-color=#c4daff></v-text-field>
                <v-text-field label="firstname" ref="firstname" id="firstname" type="text" v-model="firstName" background-color=#c4daff></v-text-field>
                <v-text-field label="middlename" ref="middlename" id="middlename" type="text" v-model="middleName"></v-text-field>
                <v-text-field label="nickname" ref="nickname" id="nickname" type="text" v-model="nickname"></v-text-field>
                <v-text-field label="email" ref="email" id="email" type="email" v-model="email" background-color=#c4daff></v-text-field>
                <v-text-field label="password" ref="password" id="password" type="password" v-model="password" background-color=#c4daff></v-text-field>
                <v-text-field label="confirm password" ref="confirm-password" id="confirm-password" type="password" v-model="confirmPassword" background-color=#c4daff></v-text-field>
                <v-text-field label="bio" ref="bio" id="bio" type="text" v-model="bio"></v-text-field>
                <v-text-field label="date of birth" ref="date-of-birth" id="date-of-birth" type="date" v-model="dateOfBirth" background-color=#c4daff></v-text-field>
                <v-select label="gender" v-model="gender" ref="gender" id="gender" :items="genders" background-color=#c4daff></v-select>
              </v-card-text>
              <v-card-actions>
                <p class="pl-1" style="color: red">{{ errorMessage }}</p>
                <v-spacer />
                <v-btn @click="saveButtonClicked" color="primary" >Register</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
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
      genders: [
        "male", "female", "non-binary"
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
        .then((user) => {
          if (user.profile_id === undefined) {
            throw new Error("created profile not returned with an id");
          }
          this.$router.push({ name: "profilePage", params: { profileId: user.profile_id.toString() } });
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

p {
    display: inline-block;
  }
</style>
