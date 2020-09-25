<template>
  <div>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="12" md="6">
          <v-card class="elevation-12" width="100%">
            <v-toolbar color="primary" dark flat> 
              <v-toolbar-title>
                <v-btn
                  dark
                  icon
                  @click="returnToProfile"
                  >
                  <v-icon>mdi-arrow-left</v-icon>
                </v-btn>
                Editing profile: {{ titleBarUserName }}</v-toolbar-title>
            </v-toolbar>

            <v-card-text>
              <v-card-title>About Me</v-card-title>
              <v-form ref="editForm">
                <v-text-field
                  id="nickname"
                  label="Nickname"
                  v-model="editedUser.nickname"
                  :rules="inputRules.nicknameRules"
                ></v-text-field>
                <v-textarea
                  id="bio"
                  label="Bio"
                  filled
                  placeholder="Tell us about yourself..."
                  v-model="editedUser.bio"
                  :rules="inputRules.bioRules"></v-textarea>
                <template>
                  <v-container-fluid>
                    <v-radio-group
                      v-model="editedUser.fitness"
                      label="Fitness Level"
                      :mandatory="false">
                      <v-radio label="No Fitness (Chocolate Muffin)" :value="0"></v-radio>
                      <v-radio label="Little Fitness (Fruit Muffin)" :value="1"></v-radio>
                      <v-radio label="Moderate Fitness (Nut Muffin)" :value="2"></v-radio>
                      <v-radio label="Enthusiastically Fit (Kale Muffin)" :value="3"></v-radio>
                      <v-radio label="Crazy Fit (No Muffin)" :value="4"></v-radio>
                      <v-radio label="Unspecified" :value="-1"></v-radio>
                    </v-radio-group>
                  </v-container-fluid>
                </template>

                <v-divider></v-divider>
                <v-card-title>Personal Details</v-card-title>

                <v-text-field
                  id="firstname"
                  label="First name"
                  v-model="editedUser.firstname"
                  :rules="inputRules.firstnameRules"
                ></v-text-field>
                <v-text-field
                  id="middlename"
                  label="Middle name"
                  v-model="editedUser.middlename"
                  :rules="inputRules.middlenameRules"
                ></v-text-field>
                <v-text-field
                  id="lastname"
                  label="Last name"
                  v-model="editedUser.lastname"
                  :rules="inputRules.lastnameRules"
                ></v-text-field>
                <template>
                  <v-container-fluid>
                    <v-radio-group
                      v-model="editedUser.gender"
                      label="Gender"
                      :mandatory="false">
                      <v-radio v-for="gender in availableGenders" :key="gender" :label="gender" :value="gender"></v-radio>
                    </v-radio-group>
                  </v-container-fluid>
                </template>

                <v-menu
                  ref="dobMenu"
                  v-model="dobMenu"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template v-slot:activator="{ on }">
                    <v-text-field
                      dense
                      v-model="editedUser.date_of_birth"
                      :value="editedUser.date_of_birth"
                      v-on="on"
                      readonly
                      label="Date of Birth"
                      :rules="inputRules.dobRules"
                    ></v-text-field>
                  </template>
                  <v-date-picker
                    no-title
                    v-model="editedUser.date_of_birth"
                    @input="dobMenu = false"
                  ></v-date-picker>
                </v-menu>
                <v-container>
                  <v-card-title>Set your Location<v-btn v-if="editedUser.location.city !== ''" @click="clearLocation" class="ml-1" text color="primary">Clear Current Location</v-btn></v-card-title>
                  <v-row>
                    <v-col cols="8" sm="4">
                      <v-text-field
                        label="City (Required)"
                        single-line
                        v-model="editedUser.location.city"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="8" sm="4">
                      <v-text-field
                        label="State (Optional)"
                        single-line
                        v-model="editedUser.location.state"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="8" sm="4">
                      <v-text-field
                        label="Country (Required)"
                        single-line
                        v-model="editedUser.location.country"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>

                <v-card-title>Passport Countries</v-card-title>

                <v-autocomplete
                  :items="passportCountries"
                  color="white"
                  item-text="name"
                  label="Countries"
                  placeholder="Start searching for countries you can visit"
                  v-model="editedUser.passports"
                  chips
                  deletable-chips
                  multiple
                ></v-autocomplete>

                <v-card-title>Interests</v-card-title>

                <v-autocomplete
                  :items="availableActivityTypes"
                  color="white"
                  item-text="name"
                  label="Interests"
                  placeholder="What activities are you interested in?"
                  v-model="editedUser.activities"
                  chips
                  deletable-chips
                  multiple
                ></v-autocomplete>
              </v-form>

              <v-row justify="end">
                <v-btn @click="returnToProfile" class="ma-1" outlined width="150">Cancel</v-btn>
                <v-btn @click="saveButtonClicked" color="primary" class="ma-1 mr-3" width="150" :loading="isSubmitting">Save profile</v-btn>
              </v-row>
              <br />
            </v-card-text>
          </v-card>
          <br />
          <v-card>
            <v-toolbar color="primary" dark flat> 
              <v-toolbar-title>Email Addresses</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
              <v-container class="fill-height" fluid>
                <v-row align="center" justify="center">
                  <v-col cols="12">
                    <!-- +1 to additional_email length because there should always be a primary email -->
                    <p>Email addresses: <b>{{ (editedUser.additional_email !== undefined && (1 + editedUser.additional_email.length)) || 0 }}/5</b></p>
                    <br />
                    <v-tooltip top>
                      <template v-slot:activator="{ on }">
                        <v-chip color="primary" v-on="on" class="ml-1 mb-2">{{ editedUser.primary_email }}</v-chip>
                      </template>
                      <span>Primary email</span>
                    </v-tooltip>
                    <v-tooltip top v-for="email in editedUser.additional_email" :key="email">
                      <template v-slot:activator="{ on }">
                        <v-chip v-on="on" class="ml-1 mb-2" close 
                          @click:close="deleteEmailAddress(email)"
                          @click="setPrimaryEmail(email)">
                          {{ email }}
                        </v-chip>
                      </template>
                      <span>Click to make primary</span>
                    </v-tooltip>
                  </v-col>
                </v-row>
                <v-row align="center" justify="center" v-if="editedUser.additional_email.length < 4">
                  <v-col cols="12" sm="12" md="6">
                    <v-text-field
                      v-model="newEmail"
                      label="New email address"
                      type="email"
                      required
                      :rules="inputRules.emailRules"
                      @keyup.enter.native="addTempEmail(newEmail)"
                    ></v-text-field>
                  </v-col>
                  <v-btn id="addEmailAddress" @click="addTempEmail(newEmail)" text>Add Email</v-btn>
                </v-row>
              </v-container>
              <br /><br />
              <v-row justify="end">
                <v-btn @click="returnToProfile" class="ma-1" outlined width="150">Cancel</v-btn>
                <v-btn id="updateEmail" @click="updateEmail()" color="primary" class="ma-1 mr-3" width="150" :loading="isSubmitting">Save emails</v-btn>
              </v-row>
            </v-card-text>
          </v-card>
          <br />
          <v-card>
            <v-toolbar color="primary" dark flat> 
              <v-toolbar-title>Change Password</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
              <v-form>
                <v-text-field
                  v-model="oldPassword"
                  label="Current password"
                  type="password"
                  dense
                  filled
                  required
                  v-if="!isAdmin"
                ></v-text-field>
                <v-text-field
                  v-model="newPassword"
                  label="New password"
                  type="password"
                  dense
                  filled
                  required
                ></v-text-field>
                <v-text-field
                  v-model="repeatPassword"
                  label="Repeat new password"
                  type="password"
                  :rules="[this.newPassword == this.repeatPassword ? true : 'Passwords do not match']"
                  dense
                  filled
                  required
                  @keyup.enter.native="updatePasswordButtonClicked"
                ></v-text-field>
                <v-alert type="error" v-if="passwordErrorMessage">{{ passwordErrorMessage }}</v-alert>
                <v-alert type="success" v-if="passwordSuccessMessage">{{ passwordSuccessMessage }}</v-alert>
                <v-row justify="end">
                  <v-btn id="updatePassword" @click="updatePasswordButtonClicked" color="primary" class="mr-3" :loading="isSubmitting">Update password</v-btn>
                </v-row>
              </v-form>
              <!-- insert edit email and edit password forms here -->
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
// eslint-disable-next-line no-unused-vars
import { UserApiFormat } from "../scripts/User";
import * as profileController from "../controllers/profile.controller";
import * as activityController from "../controllers/activity.controller";
import FormValidator from "../scripts/FormValidator";
// eslint-disable-next-line no-unused-vars
import { RegisterFormData } from "../controllers/register.controller";
import {updateActivityTypes} from "../models/user.model"
import { clearAuthInfo, isAdmin } from "../services/auth.service";
// app Vue instance
const Homepage = Vue.extend({
  name: "Homepage",

  // app initial state
  data: function() {
    let formValidator = new FormValidator();
    return {
      titleBarUserName: "",
      isAdmin: false as Boolean,
      currentProfileId: NaN as number,
      editedUser: {} as UserApiFormat,
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
        bioRules: [
          (v: string) =>
            formValidator.checkBioValidity(v) || formValidator.BIO_ERROR_STRING
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
        ]
      },

      // values pertaining Personal Details section
      passportCountries: [] as Array<string>,
      activityTypes: [],
      selectedActivity: "" as string,
      selectedCountry: "" as string,
      availableGenders: ["male", "female", "non-binary"], // casing is dependent on API spec
      availableActivityTypes: [] as string[],
      selectedActivityType: "" as string,
      dobMenu: false,
      newEmail: "" as string,
      oldPassword: "",
      newPassword: "",
      repeatPassword: "",
      passwordErrorMessage: "",
      passwordSuccessMessage: "",
      confirmDeleteModal: false,
      isSubmitting: false as boolean
    };
  },

  /**
   * runs when the page is created
   */
  created() {
    // load profile info
    const profileId: number = parseInt(this.$route.params.profileId);
    if (isNaN(profileId)) {
      // profile id in route not a number
      this.$router.push({ name: "login" });
    }
    this.isAdmin = isAdmin();
    this.currentProfileId = profileId;
    profileController.fetchProfileWithId(profileId)
      .then(user => {
        this.titleBarUserName = `${user.firstname} ${user.lastname}`;
        this.editedUser = user;
        if (user.fitness) {
          this.editedUser.fitness = user.fitness;
        } else {
          this.editedUser.fitness = -1;
        }
        if(user.location == undefined) {
          user.location = {
            city: "",
            state: "",
            country: ""
          }
        }
      })
      .catch(err => {
        console.error(err);
      });

    profileController.getAvailablePassportCountries()
      .then(countries => {
        this.passportCountries = countries;
      })
      .catch(err => {
        console.error("unable to load passport countries");
        console.error(err);
      });

    activityController
      .getAvailableActivityTypes()
      .then(activityTypes => {
        this.availableActivityTypes = activityTypes;

        
      })
      .catch(err => {
        console.error("unable to load activity types");
        console.error(err);
      });
  },

  /**
   * handles the behaviour when the user navigates to another route that uses this component.
   * will refresh the page to load the new information.
   */
  beforeRouteUpdate(to, _, next) {
    const profileId: number = parseInt(to.params.profileId);
    if (isNaN(profileId)) {
      console.error(to.params.profileId + " is not a valid profile id")
      return
    }
    next()
  },

  methods: {
    /**
     * adds the passport country selected in the dropdown to the current user's passport countries
     */
    addSelectedPassportCountry: function() {
      if (!this.selectedCountry) {
        return;
      }
      profileController.addPassportCountry(
        this.selectedCountry,
        this.editedUser
      );
      this.selectedCountry = "";
    },

    addActivityType: function() {
      if (!this.selectedActivity) {
        return
      }
      if (!this.editedUser.activities) {
        this.editedUser.activities = []
      }
      
      this.editedUser.activities.push(this.selectedActivity);
    },
    updateActivityType: function(){
      if (!this.editedUser.activities) {
        this.editedUser.activities = []
      }
      updateActivityTypes(this.editedUser.activities, this.currentProfileId)
      .then(() => {this.returnToProfile()})
      .catch(() => {
        alert("Unable to update user. ");
      });
    },

    removeActivityType:function(activitySelected: string){
      if (!this.editedUser.activities) {
        this.editedUser.activities = []
      }
      for (let i = 0; i < this.editedUser.activities.length; i++) {
        if (this.editedUser.activities[i] === activitySelected) {
          this.editedUser.activities.splice(i, 1);
        }
      }

    },
    /**
     * removes the given country from the passport countries of the user being edited
     */
    deletePassportCountry: function(country: string) {
      profileController.deletePassportCountry(country, this.editedUser);
    },

    /**
     * adds the activity type selected in the dropdown to the current user's activity types
     */
    addSelectedActivityType: async function() {
      if (!this.selectedActivityType) {
        return;
      }
      await profileController.addActivityType(
        this.selectedActivityType,
        this.editedUser
      );
    },

    /**
     * removes the given activity type from the interests of the user being edited
     */
    deleteActivityType: function(activityType: string) {
      profileController.deleteActivityType(activityType, this.editedUser);
    },
    clearLocation: function(){
      this.editedUser.location = undefined;
      this.editedUser.location = {
            city: "",
            state: "",
            country: ""
          }
    },
    /**
     * persists the changes made on the edit page by the user
     */
    saveButtonClicked: async function() {
      if (
        (this.$refs.editForm as Vue & { validate: () => boolean }).validate()
      ) {
        this.isSubmitting = true;
        profileController
          .persistChangesToProfile(this.editedUser, this.currentProfileId)
          .then(() => {
            this.returnToProfile();
          })
          .catch((e) => {
            alert(e.message);
          })
          .finally(() => {
            this.isSubmitting = false;
            this.$root.$emit('refreshMapAndPins');
          })
      }
    },

    /**
     * returns the user to the profile page of the profile they are editing
     */
    returnToProfile: function() {
      this.$router.push("/profiles/" + this.currentProfileId);
    },

    /**
     * saves the new state of the user's primary email and additional email list by making a http request to the backend
     */
    updateEmail: function() {
      if (this.editedUser.primary_email !== undefined) {
         if (this.editedUser.additional_email === undefined) {
           this.editedUser.additional_email = [];
         }
         this.isSubmitting = true;
        profileController.updateUserEmails(this.editedUser.primary_email, this.editedUser.additional_email, this.currentProfileId)
          .then(() => {
            // refresh the page after updating emails
            history.go(0);
          })
          .catch(err => console.log(err))
          .finally(() => {this.isSubmitting = false;});
      }
    },

    /**
     * sets the primary email of the user to the email string specified if a valid email
     * places the old primary email into the user's list of additional emails
     */
    setPrimaryEmail: function(email: string) {
      let oldPrimaryEmail = this.editedUser.primary_email;
      this.editedUser.primary_email = email;

      if (this.editedUser.additional_email === undefined) {
        if (oldPrimaryEmail !== undefined) {
          this.editedUser.additional_email = [oldPrimaryEmail];
        } else {
          this.editedUser.additional_email = [];
        }

      } else {
        let index = this.editedUser.additional_email.indexOf(email);
        if (oldPrimaryEmail !== undefined) {
          this.editedUser.additional_email.splice(index, 1, oldPrimaryEmail);
        } else {
          this.editedUser.additional_email.splice(index, 1);
        }
      }
    },

    /**
     * remove the specified email from the locally stored list of user emails to be updated if the user clicks "Save email changes"
     */
    deleteEmailAddress: function(email: string) {
      if (this.editedUser.additional_email === undefined) {
        this.editedUser.additional_email = [];
      }
      let index = this.editedUser.additional_email.indexOf(email);
      this.editedUser.additional_email.splice(index, 1);
    },

    /**
     * adds an email to the user's list of additional emails in the view to be saved if the user clicks "Save email changes"
     */
    addTempEmail: function(email: string) {
      if (new FormValidator().isValidEmail(email) && this.editedUser.primary_email != email &&
          !(this.editedUser.additional_email && this.editedUser.additional_email.includes(email))) {
        if (this.editedUser.additional_email === undefined) {
          this.editedUser.additional_email = [email];
        } else {
          this.editedUser.additional_email.push(email);
        }
        this.newEmail = "";
      }
    },

    updatePasswordButtonClicked: function() {
      this.passwordSuccessMessage = "";
      this.passwordErrorMessage = "";

      if (this.newPassword != this.repeatPassword) return;

      this.isSubmitting = true;
      profileController
        .updatePassword(
          this.oldPassword,
          this.newPassword,
          this.repeatPassword,
          this.currentProfileId
        )
        .then(() => {
          this.passwordSuccessMessage = "Password changed successfully";
        })
        .catch((err: any) => {
          this.passwordErrorMessage = err.message;
        })
        .finally(() => {
          this.isSubmitting = false;
        });

      this.oldPassword = "";
      this.newPassword = "";
      this.repeatPassword = "";
    },
    /**
     * Delete the account of the user currently being looked at
     * Can only be done by an admin or the user themself
     */
    deleteAccount: function() {
        profileController.deleteUserAccount(this.currentProfileId)
        .then(() => {
          clearAuthInfo();
          this.$router.push({ name: "register" });
        })
        .catch((err) => {
          if (!err.response || !err.response.status) {
            console.log(err)
          } else {
            console.log(err.response.status)
            // May wish to eventually take different actions depending on type of error returned from server (once implemented)
            // E.g. If the server decides to require a password and returns a 400 when password is wrong
            // I.e. Basically, warn the user if the type of error means that the account has not actually been deleted
          }
        })
    }
  }
});

export default Homepage;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>
