/* eslint-env jest */

import Vue from 'vue'
import Vuetify from 'vuetify'
import Homepage from "../Homepage.vue";
import CreateActivity from "../CreateActivity.vue";

import {mount, createLocalVue} from '@vue/test-utils'

import {
  fetchProfileWithId,checkAuth
} from "../../controllers/profile.controller.ts";
jest.mock("../../controllers/profile.controller.ts")
import {
  getActivitiesByCreator, getContinuousActivities, getDurationActivities
} from "../../controllers/activity.controller";

jest.mock("../../controllers/activity.controller.ts")

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
    checkAuth.mockResolvedValue(true)
    
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
      expect(wrapper.find('#editButton').exists()).toBe(true) 
    })

    // need to sort out how to test routing??!?!?

    // it('Clicking on the create activity button takes you to activity screen', async () => { 
    //   const button = wrapper.find('#createActivityButton')
    //   const event = jest.fn()
    //   wrapper.vm.$on('action-btn:clicked',event)
    //   expect(event).toHaveBeenCalledTimes(0)
    //   // wrapper.find('#createActivityButton').trigger('click') //click the createActivityButton
    //   button.trigger('click')
    //   await wrapper.vm.$nextTick() //wait for page to load.
    //   expect(event).toHaveBeenCalledTimes(1)
    // })

    it('Check that the user is authenticated, can see createActivity Button', async () => { 
      expect(wrapper.find('#createActivityButton').exists()).toBe(true) 
    })


    it('Activities are loaded, and the homepage has a duration button', async () => { 
      expect(wrapper.find('#Duration').exists()).toBe(true) 


    })
    it('Activities are loaded, and the homepage has a continuous button', async () => { 
      expect(wrapper.find('#Continuous').exists()).toBe(true) 

    })

  })
