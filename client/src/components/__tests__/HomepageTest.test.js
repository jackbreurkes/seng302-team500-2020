/* eslint-env jest */

import Vue from 'vue'
import Vuetify from 'vuetify'
import Homepage from "../Homepage.vue";

import {mount, createLocalVue} from '@vue/test-utils'

import {
  fetchProfileWithId,
} from "../../controllers/profile.controller.ts";
jest.mock("../../controllers/profile.controller.ts")
import {
  getActivitiesByCreator, getContinuousActivities, getDurationActivities
} from "../../controllers/activity.controller";
jest.mock("../../controllers/activity.controller.ts")

import { getMyUserId } from "../../services/auth.service";
jest.mock("../../services/auth.service")

Vue.use(Vuetify)
const localVue = createLocalVue()

const mockProfileId = 7001;
const mocks = { //mock route
    $route: {
      params: {
        profileId: mockProfileId,
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
    getActivitiesByCreator.mockResolvedValue(
        {
          "activity_id":3,
          "activity_name":"create continuous",
          "continuous":true,
          "description":"wadawdadwwad",
          "location":"wad",
          "creator_id":2,
          "activity_type":["Running"]
        },

        {
          "activity_id":4,
          "activity_name":"durationboi",
          "continuous":false,
          "start_time":"2020-08-05T11:11:00+1200",
          "end_time":"2020-08-07T11:11:00+1200",
          "description":"awdawdadw",
          "location":"awd",
          "creator_id":2,
          "activity_type":["Running"]
        }
      
      )   
    getDurationActivities.mockResolvedValue(
      {
        "activity_id":4,
        "activity_name":"durationboi",
        "continuous":false,
        "start_time":"2020-08-05T11:11:00+1200",
        "end_time":"2020-08-07T11:11:00+1200",
        "description":"awdawdadw",
        "location":"awd",
        "creator_id":2,
        "activity_type":["Running"]
      }
    )
    getContinuousActivities.mockResolvedValue(
        {
          "activity_id":3,
          "activity_name":"create continuous",
          "continuous":true,
          "description":"wadawdadwwad",
          "location":"wad",
          "creator_id":2,
          "activity_type":["Running"]
        }
    )
    getMyUserId.mockImplementation(() => mockProfileId)

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
    
    it('has a edit profile button', async () => { 
      wrapper.find('#profileDropDown').trigger('click') //open the profile option dropdown
      await wrapper.vm.$nextTick() //wait for the dropdown to be rendered.
      // console.log(wrapper.html()) //how to view the wrapper
      expect(wrapper.find('#editButton').exists()).toBe(true) 
    })
    it('Activities are loaded, and the homepage has a duration button', async () => { 
      expect(wrapper.find('#Duration').exists()).toBe(true) 

    })
    it('Activities are loaded, and the homepage has a continuous button', async () => { 
      expect(wrapper.find('#Continuous').exists()).toBe(true) 
    })

  })
