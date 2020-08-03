/* eslint-env jest */

import Vue from 'vue'
import Vuetify from 'vuetify'
import Homepage from "../Homepage.vue";

import {mount, createLocalVue} from '@vue/test-utils'

import {
  fetchProfileWithId,
} from "../../controllers/profile.controller.ts";
jest.mock("../../controllers/profile.controller.ts")

Vue.use(Vuetify)
const localVue = createLocalVue()

const mockProfileId = 7001;
const mocks = { //mock route
    $route: {
      params: {
        profileId: mockProfileId,
        currentlyHasAuthority: true
      }
   },
    $router: { //mocks router, use if checking for directing to another page
      push: jest.fn(),
      currentRoute: {
        path: "",
        name: ""
      }
    }
  }

const stubs = ['router-link']
  

describe("homepageTests", () => {

    let wrapper 

    fetchProfileWithId.mockResolvedValue( //mocks fetchProfileWithId, give fake info to display
      {
        "profile_id":mockProfileId,
        "permission_level":0,
        "lastname":"yee",
        "firstname":"josh",
        "middlename":null,
        "nickname":null,
        "primary_email":"joshjoshjosh@josh.com",
        "bio":null,
        "date_of_birth":"1998-11-11",
        "gender":"male",
        "fitness":-1,
        "passports":[],
        "additional_email":[],
        "activities":[]
    }
    )    
  
    beforeEach(async function() {
      let vuetify = new Vuetify()
        wrapper = mount(Homepage, { 
        localVue,
        vuetify,
        mocks,
        sync: false,
        stubs
      })
    })
    
    it('has a edit profile button', async () => {      //test to see if the edit button exists
      wrapper.find('#profileDropDown').trigger('click')
      await wrapper.vm.$nextTick()
      //console.log(wrapper.html())
      console.log(wrapper.html())
      expect(wrapper.find('#editButton').exists()).toBe(true)
    })

  })
  