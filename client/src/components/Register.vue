<template>
  <div id="Register">
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col sm="12" md="9" lg="6">
          <v-form ref="editForm" submit="false" v-model="valid">
            <v-card class="elevation-12" width=100%>
              <v-toolbar
                color="primary"
                dark
                flat
              >
                <v-toolbar-title>Register</v-toolbar-title>   
              </v-toolbar>
              <v-card-text>
                <v-row>
                  <p>Already registered?</p>
                  <router-link to="/login">
                    <p class="pl-1">Log In</p>
                  </router-link> 
                </v-row>
                <v-row>
                  <p class="text--disabled">Mandatory fields <span style="color: #ff5555;">*</span></p>
                </v-row>
                <v-row>
                  <v-col lg="4" md="4" sm="12">
                    <v-text-field label="First name" ref="firstname" id="firstname" type="text" v-model="firstName" class="required" :rules="inputRules.firstnameRules"></v-text-field>
                  </v-col>
                  <v-col lg="4" md="4" sm="12">
                    <v-text-field label="Middle name" ref="middlename" id="middlename" type="text" v-model="middleName" :rules="inputRules.middlenameRules"></v-text-field>
                  </v-col>
                  <v-col lg="4" md="4" sm="12">
                    <v-text-field label="Last name" ref="lastname" id="lastname" type="text" v-model="lastName" class="required" :rules="inputRules.lastnameRules"></v-text-field>
                  </v-col>
                </v-row>
                <v-row>
                  <v-col lg="4" md="4" sm="12">
                    <v-text-field label="Nickname" ref="nickname" id="nickname" type="text" v-model="nickname" :rules="inputRules.nicknameRules"></v-text-field>
                  </v-col>
                  <v-col lg="4" md="4" sm="12">
                    <v-select label="Gender" v-model="gender" ref="gender" id="gender" :items="genders" item-text="name" item-value="value" class="required" :rules="inputRules.genderRules"></v-select>
                  </v-col>
                  <v-col lg="4" md="4" sm="12">
                    <v-text-field label="Date of Birth" ref="date-of-birth" id="date-of-birth" type="date" v-model="dateOfBirth" class="required" :rules="inputRules.dobRules"></v-text-field>
                  </v-col>
                </v-row>
                <v-row>
                  <v-text-field label="Email address" ref="email" id="email" type="email" v-model="email" class="required ma-3" :rules="inputRules.emailRules"></v-text-field>
                </v-row>
                <v-row>
                  <v-text-field label="Password" ref="password" id="password" type="password" v-model="password" class="required ma-3" :rules="inputRules.passwordRules"></v-text-field>
                </v-row>
                <v-row>
                  <v-text-field label="Confirm password" ref="confirm-password" id="confirm-password" class="required ma-3" type="password" v-model="confirmPassword" @keyup.enter.native="saveButtonClicked" :rules="[this.password == this.confirmPassword ? true : 'Passwords do not match']"></v-text-field>
                </v-row>
                <v-row>
                  <p class="pl-1" style="color: red">{{ errorMessage }}</p>
                </v-row>
                <v-row>
                  <v-col lg="6" md="6" sm="12" offset-sm="0" offset-md="3">
                    <v-btn @click="saveButtonClicked" color="primary" block class="pt-6 pb-6" :disabled="!valid">Register</v-btn>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </v-form> 
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { tagMandatoryAttributes } from "../scripts/LoginRegisterHelpers";
// eslint-disable-next-line no-unused-vars
import { registerUser, RegisterFormData } from "../controllers/register.controller";
import Vue from 'vue'
import FormValidator from "../scripts/FormValidator";

// app Vue instance
const Register = Vue.extend({
  name: "Register",

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
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
        {name: "Male", value: "male"}, 
        {name: "Female", value: "female"},
        {name: "Non-binary", value: "non-binary"}
      ],
      lastName: "",
      firstName: "",
      middleName: "",
      nickname: "",
      email: "",
      password: "",
      confirmPassword: "",
      dateOfBirth: "",
      gender: "",
      valid: false,
      errorMessage: "",

      inputRules: {
        lastnameRules: [
          (v: string) =>
            formValidator.checkLastnameValidity(v) ||
            formValidator.LAST_NAME_ERROR_STRING
        ],
        firstnameRules: [
          (v: string) =>
            formValidator.checkFirstnameValidity(v) ||
            formValidator.FIRST_NAME_ERROR_STRING
        ],
        middlenameRules: [
          (v: string) =>
            formValidator.checkMiddlenameValidity(v) ||
            formValidator.MIDDLE_NAME_ERROR_STRING
        ],
        nicknameRules: [
          (v: string) =>
            formValidator.checkNicknameValidity(v) ||
            formValidator.NICKNAME_ERROR_STRING
        ],
        dobRules: [
          (v: string) =>
            formValidator.checkDobValidity(v) || formValidator.DOB_ERROR_STRING
        ],
        genderRules: [
          (v: string) =>
            formValidator.checkGenderValidity(v) ||
            formValidator.GENDER_ERROR_STRING
        ],
        emailRules: [
          (v: string) =>
            formValidator.isValidEmail(v) || formValidator.EMAIL_ERROR_STRING          
        ],
        passwordRules: [
          (v: string) =>
          formValidator.checkPasswordValidity(v) ||
          formValidator.PASSWORD_ERROR_STRING
        ]
      },
    };
  },

  mounted: function() {
    let refs = this.$refs;
    tagMandatoryAttributes(refs, this.mandatoryAttributes);
  },

  methods: {
    saveButtonClicked() {
      if (
        (this.$refs.editForm as Vue & { validate: () => boolean }).validate()
      ) {
        let formData: RegisterFormData = {
          firstName: this.firstName,
          lastName: this.lastName,
          middleName: this.middleName,
          nickname: this.nickname,
          email: this.email,
          password: this.password,
          confirmPassword: this.confirmPassword,
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
        this.errorMessage = "Submitting...";
      }
    }
  }
});

export default Register;
</script>

<style>
[v-cloak] {
  display: none;
}

p {
    display: inline-block;
}

.required label::after {
    content: "*";
    color: #ff8888;
}
</style>
