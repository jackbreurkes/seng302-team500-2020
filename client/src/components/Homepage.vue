<template>
  <div>

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
            <v-toolbar-title>Profile: {{ currentUser.firstname }} {{ currentUser.lastname }} </v-toolbar-title>
            </v-toolbar>

      <!-- as per U3 AC3, user knows the limit of additional emails -->





       <v-card-text>
      <div v-if="!editing">
        <p>First Name: {{ currentUser.firstname }}</p><br>
        <p>Middle name: {{currentUser.middlename}}</p><br>
        <p>Last Name: {{ currentUser.lastname }} </p><br>
        <p>Nickname: {{ currentUser.nickname }} </p><br>
        <p>Date of Birth: {{ currentUser.date_of_birth }} </p><br>
        <p>Bio: {{ currentUser.bio }} </p><br>
        <p>Gender: {{ currentUser.gender }} </p><br>
        <br>
  

        <label for="countryDropdown">Select a Country:</label>
        <select id="countryDropdown" v-model="selectedCountry">
          <option value="" selected disabled>choose a country</option>
          <option value="" v-if="passportCountries.length === 0" disabled>please wait...</option>
          <option v-for="country in passportCountries" :key="country.numericCode" :value="country">{{ country.name }}</option>
        </select>
        <button id="selectCountry" @click="selectCountry">Select</button>
        <v-list id="passports" dense>
           <v-subheader>Current passports:</v-subheader>
           <v-list-item-group v-model="selectedPassport" color="primary">
            <v-list-item v-for="passport in currentUser.passports" :key="passport">
              <v-list-item-content>
                <v-list-item-title v-text="passport"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
           </v-list-item-group>
        </v-list>
        <v-btn @click="deletePassportCountry" small>Delete selected passport</v-btn>
        <br>
        <br>
        <label for="fitnessDropdown">Select a Fitness Level:</label>
        <select id="fitnessDropdown" v-model="selectedFitnessLevel">
          <option value=-1>Not specified</option>
          <option value=0>Muffin -No fitness</option>
          <option value=1>Potato -Little fitness</option>
          <option value=2>Carrot -Moderate fitness</option>
          <option value=3>Blueberry -Outdoors enthusiast</option>
          <option value=4>Kale -Professional athlete</option>
        </select>
        <v-btn id="selectFitness" @click="selectFitnessLevel">Select Level of fitness</v-btn>
        <br>

        <p>Primary email: {{ currentUser.primary_email }}</p>
        <!-- New Email input field and button -->
        <br>
        <p>Secondary Emails {{ currentUser.additional_email.length }} / 5:</p>
        <ul>
          <li v-for="email in currentUser.additional_email" :key="email">{{ email }}
          <v-btn @click="deleteEmailAddress(email)">delete</v-btn>
          <v-btn @click="setPrimaryEmail(email)">Make Primary</v-btn>
          </li>
        </ul>
        <br>
        <p>Add a new email: </p>
        <!-- <input ref="newEmail" id="newEmail" type="email" v-model="newEmail" /> -->
        <v-card-actions>
        <v-text-field
        v-model="newEmail"
        label="enter new email here"
        type="email"
        dense
        filled
        required></v-text-field>
        
        <v-btn id="addEmailAddress"  @click="addEmailAddress" >Add Email</v-btn>
        </v-card-actions>
        <br>
        <br>
        <v-btn @click="editProfile">Edit Profile</v-btn>
        <v-btn @click="logoutButtonClicked">Logout</v-btn>
      </div>

      <div v-if="editing">
        <v-form ref="editForm">
          <v-text-field id="firstname" label="First name" v-model="editedUser.firstname" :rules="inputRules.firstnameRules"></v-text-field>
          <v-text-field id="middlename" label="Middle name" v-model="editedUser.middlename" :rules="inputRules.middlenameRules"></v-text-field>
          <v-text-field id="lastname" label="Last name" v-model="editedUser.lastname" :rules="inputRules.lastnameRules"></v-text-field>
          <v-text-field id="nickname" label="Nickname" v-model="editedUser.nickname" :rules="inputRules.nicknameRules"></v-text-field>
          <v-text-field id="bio" label="Bio" v-model="editedUser.bio" :rules="inputRules.bioRules"></v-text-field>
          <v-menu>
            <template v-slot:activator="{ on }">
            <v-text-field v-model="editedUser.dateOfBirth" :value="currentUser.dateOfBirth" v-on="on" label="Date of Birth" :rules="inputRules.dobRules"></v-text-field>
            </template>
            <v-date-picker v-model="editedUser.dateOfBirth"></v-date-picker>
          </v-menu>
          <v-select label="Gender" v-model="editedUser.gender" :items="genders" :rules="inputRules.genderRules"></v-select>
        </v-form>

        <button @click="saveButtonClicked">Save</button>
        <button @click="cancelButtonClicked">Cancel</button>
      </div>

      <br>
      <v-form>
      <v-text-field
        v-model="oldPassword"
        label="old password"
        type="password"
        dense
        filled
        required></v-text-field>
      <v-text-field
        v-model="newPassword"
        label="new password"
        type="password"
                dense
        filled
        required></v-text-field>
      <v-text-field
        v-model="repeatPassword"
        label="repeat password"
        type="password"
        dense
        filled
        required></v-text-field>
    </v-form>
      <v-alert type="error" v-if="passwordErrorMessage">{{ passwordErrorMessage }}</v-alert>
      <v-alert type="success" v-if="passwordSuccessMessage">{{ passwordSuccessMessage }}</v-alert>
      <v-btn id="updatePassword" @click="updatePasswordButtonClicked">Update your password</v-btn>

      <br>
      <br>





      <div v-if="editing">
        <v-form>
          <v-text-field dense filled id="firstname" label="First name" v-model="currentUser.firstname" :rules="inputRules.firstnameRules"></v-text-field>
          <v-text-field dense filled id="middlename" label="Middle name" v-model="currentUser.middlename" :rules="inputRules.middlenameRules"></v-text-field>
          <v-text-field dense filled id="lastname" label="Last name" v-model="currentUser.lastname" :rules="inputRules.lastnameRules"></v-text-field>
          <v-text-field dense filled id="nickname" label="Nickname" v-model="currentUser.nickname" :rules="inputRules.nicknameRules"></v-text-field>
          <v-text-field dense filled id="bio" label="Bio" v-model="currentUser.bio" :rules="inputRules.bioRules"></v-text-field>
          <v-menu>
            <template v-slot:activator="{ on }">
            <v-text-field dense filled  v-model="currentUser.dateOfBirth" :value="currentUser.dateOfBirth" v-on="on" label="Date of Birth" :rules="inputRules.dobRules"></v-text-field>
            </template>
            <v-date-picker v-model="currentUser.dateOfBirth"></v-date-picker>
          </v-menu>
          <v-select dense filled  label="Gender" v-model="currentUser.gender" :items="genders" :rules="inputRules.genderRules"></v-select>
        </v-form>

        <v-btn @click="saveButtonClicked">Save</v-btn>
        <v-btn @click="cancelButtonClicked">Cancel</v-btn>
        
      
      </div>
       </v-card-text>
        </v-card>
      </v-col>
    </v-row>
</v-container>
  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  // eslint-disable-next-line no-unused-vars
  import { UserApiFormat} from '../scripts/User'
  import { logoutCurrentUser, updatePassword, addPassportCountry, deletePassportCountry, fetchCurrentUser, setFitnessLevel, editProfile, addNewEmail, deleteEmail, setPrimary } from '../controllers/profile.controller'
  import { checkFirstnameValidity, checkLastnameValidity, checkMiddlenameValidity, checkNicknameValidity, checkBioValidity, checkDobValidity, checkGenderValidity, isValidEmail } from '../scripts/FormValidator'
  // eslint-disable-next-line no-unused-vars
  import { RegisterFormData } from '../controllers/register.controller';


  // app Vue instance
const Homepage =  Vue.extend({
    name: 'Homepage',

    // app initial state
    data: function() {
      return {
        currentUser: {} as UserApiFormat,
        passportCountries: [],
        selectedCountry: "" as any,
        selectedPassport: 0,
        selectedFitnessLevel: -1,
        newEmail: "",
        email: "",
        oldPassword: '',
        newPassword: '',
        repeatPassword: '',
        passwordErrorMessage: '',
        passwordSuccessMessage: '',
        genders: ["Male", "Female", "Non-binary"],
        editing: false,
        editedUser: {} as UserApiFormat,
        inputRules: {
          "lastnameRules": [(v: string) => checkLastnameValidity(v)],
          "firstnameRules": [(v: string) => checkFirstnameValidity(v)],
          "middlenameRules": [(v: string) => checkMiddlenameValidity(v)],
          "nicknameRules": [(v: string) => checkNicknameValidity(v)],
          "bioRules": [(v: string) => checkBioValidity(v)],
          "dobRules": [(v: string) => checkDobValidity(v)],
          "genderRules": [(v: string) => checkGenderValidity(v)]
        },
      }
    },

    created() {
      fetchCurrentUser()
        .then((user) => {
          this.currentUser = user;
          if (this.currentUser) {
            this.selectedFitnessLevel = this.currentUser.fitness || -1;
          }
        })
        .catch((err) => {
          console.error(err);
        });

      const Http = new XMLHttpRequest();
      const url='https://restcountries.eu/rest/v2/all';
      Http.open("GET", url, true);
      Http.send();

      Http.onreadystatechange = () => {
        try {
          const countries = JSON.parse(Http.responseText)
          this.passportCountries = countries
        } catch (err) {
          console.error(err)
        }
      }
    },

    methods: {
      updatePasswordButtonClicked: function(){
        this.passwordSuccessMessage = "";
        this.passwordErrorMessage = "";
        updatePassword(this.oldPassword,this.newPassword,this.repeatPassword)
          .then(() => {
            this.passwordSuccessMessage = "password changed successfully";
          })
          .catch((err: any) => {
            this.passwordErrorMessage = err.message;
          })

      },

      //click login button
      logoutButtonClicked: function() {
        logoutCurrentUser()
          .then(() => {
            this.$router.push({name: "login"});
          })
          .catch((err: any) => {
            console.error(err);
            this.$router.push({name: "login"});
          })
      },

      //add passport country
      selectCountry: function () {

        if (this.currentUser && this.currentUser.primary_email) {
          addPassportCountry(this.selectedCountry, this.currentUser.primary_email)
            .then(() => {
              console.log('passport country added')
              history.go(0);
            })
            .catch((err: any) => {
              console.log(err)
            })
            // refresh page after adding passport
        }
      },

      deletePassportCountry: function () {
        if (this.currentUser && this.currentUser.primary_email && this.currentUser.passports && this.currentUser.passports[this.selectedPassport]) {
          deletePassportCountry(this.currentUser.passports[this.selectedPassport], this.currentUser.primary_email)
            .then(() => {
              console.log('passport country deleted')
            // refresh page after removing passport
              history.go(0);
            })
            .catch((err: any) => {
              console.log(err)
            })
        }
      },

      selectFitnessLevel: function () {
        if (this.currentUser && this.currentUser.profile_id) {
          setFitnessLevel(this.selectedFitnessLevel, this.currentUser.profile_id)
          .then(() => {
            console.log("Fitness level set");
          })
          .catch((err: any) => {
            console.log(err);
          })
        }
      },

      addEmailAddress: function() {
        if(isValidEmail(this.newEmail)) {
          addNewEmail(this.newEmail)
          .then(() => {
            console.log("Email address added");
          })
          .catch((err) => {
            console.log(err);
          })
          // refresh page after adding emails
          //history.go(0);
        } else {
          alert("Not valid email")
        }
      },

      deleteEmailAddress: function (email: string) {
        deleteEmail(email)
        .then(() => {
          console.log("Email address deleted");
        })
        .catch((err: any) => {
          console.log(err);
        })
        // refresh page after deleting emails
        //history.go(0);
      },

      setPrimaryEmail: function(email: string) {
        setPrimary(email)
        .then(() => {
          console.log("primary email changed");
        })
        .catch((err) => {
          console.log(err);
        })
        // refresh page after changing primary email
        //history.go(0);
      },

      editProfile: function() {
        Object.assign(this.editedUser, this.currentUser);
        this.editing = true;
      },

      saveButtonClicked: async function() {
        if ((this.$refs.editForm as Vue & { validate: () => boolean }).validate()) {
          try {
            await editProfile(this.editedUser);
            Object.assign(this.currentUser, this.editedUser);
            this.editing = false;
          } catch {
            alert("Unable to update user.");
          }
        }
      },

      cancelButtonClicked: function() {
        this.editing = false;
      }
    }
  })

  export default Homepage
</script>

<style>
  [v-cloak] { display: none; }
</style>
