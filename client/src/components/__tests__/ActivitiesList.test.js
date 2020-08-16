/* eslint-env jest */

import Vue from 'vue'
import Vuetify from 'vuetify'
import ActivitiesList from "../ActivitiesList.vue";

import { mount, createLocalVue } from '@vue/test-utils'

import {
  fetchProfileWithId,
} from "../../controllers/profile.controller.ts";
jest.mock("../../controllers/profile.controller.ts")
import {
  getIsOrganising, getIsParticipating, getIsFollowingActivity
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


describe("Activities card test", () => {

  let wrapper

  fetchProfileWithId.mockResolvedValue( //mocks fetchProfileWithId, give fake info to display
    {
      "profile_id": mockProfileId,
      "permission_level": 0,
      "lastname": "yee",
      "firstname": "josh",
      "middlename": null,
      "nickname": null,
      "primary_email": "joshjoshjosh@josh.com",
      "bio": null,
      "date_of_birth": "1998-11-11",
      "gender": "male",
      "fitness": -1,
      "passports": [],
      "additional_email": [],
      "activities": []
    }
  )
  getIsOrganising.mockResolvedValue(false);
  getIsParticipating.mockResolvedValue(false);
  getIsFollowingActivity.mockResolvedValue(false);
  getMyUserId.mockImplementation(() => mockProfileId);

  beforeEach(mountFunction)

  afterEach(() => {
    jest.clearAllMocks();
  })

  function mountFunction() {
    let vuetify = new Vuetify()
    wrapper = mount(ActivitiesList, {
      localVue,
      vuetify,
      mocks,
      sync: false,
      stubs,
      propsData: {
        profileId: 7001,
        authority: true,
        activities: [
          {
            "activity_id": 4,
            "activity_name": "durationboi",
            "continuous": false,
            "start_time": "2020-08-05T11:11:00+1200",
            "end_time": "2020-08-07T11:11:00+1200",
            "description": "awdawdadw",
            "location": "awd",
            "creator_id": mockProfileId,
            "activity_type": ["Running"]
          }
        ]
      }
    }
    )
  }

  it('the creator of the activity can see the creator tag', async () => {
    expect(wrapper.find('#creatorChip').exists()).toBe(true)
  })

  it('an organiser of the activity can see the organiser tag', async () => {
    getIsOrganising.mockResolvedValue(true);
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick()
    expect(wrapper.find('#organiserChip').exists()).toBe(true)
  })

  it('a participant of the activity can see the participant tag', async () => {
    getIsParticipating.mockResolvedValue(true);
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick()
    expect(wrapper.find('#participantChip').exists()).toBe(true)
  })

  it('a follower of the activity can see the follower tag', async () => {
    getIsFollowingActivity.mockResolvedValue(true);
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick()
    expect(wrapper.find('#followerChip').exists()).toBe(true)
  })


})

