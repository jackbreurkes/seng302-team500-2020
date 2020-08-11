/* eslint-env jest */

import Vue from 'vue'
import Vuetify from 'vuetify'
import Activity from "../Activity.vue";

import { mount, createLocalVue } from '@vue/test-utils'

import {
  fetchProfileWithId,
} from "../../controllers/profile.controller.ts";
jest.mock("../../controllers/profile.controller.ts")

import {
  getActivity, getIsFollowingActivity, followActivity, unfollowActivity
} from "../../controllers/activity.controller";
jest.mock("../../controllers/activity.controller.ts")

import { getMyUserId } from "../../services/auth.service";
jest.mock("../../services/auth.service")

Vue.use(Vuetify)
const localVue = createLocalVue()
const mockProfileId = 7001;
const mockActivityId = 5;

const mocks = { //mock route
  $route: {
    params: {
      profileId: mockProfileId,
      activityId: mockActivityId
    }
  },
  $router: { //mocks router, use if checking for directing to another page
    push: jest.fn(),
    currentRoute: {
      path: "",
      name: ""
    },
    back: jest.fn()
  }
}

const stubs = ['router-link']


describe("activityPageTests", () => {

  let wrapper

  fetchProfileWithId.mockResolvedValue( //mocks fetchProfileWithId, give fake info to display
    {
      "profile_id": mockProfileId,
      "permission_level": 0,
      "lastname": "breurkes",
      "firstname": "jash",
      "middlename": null,
      "nickname": null,
      "primary_email": "jack@josh.com",
      "bio": null,
      "date_of_birth": "1998-11-11",
      "gender": "male",
      "fitness": -1,
      "passports": [],
      "additional_email": [],
      "activities": []
    }
  )
  getActivity.mockResolvedValue(
    {
      "activity_id": mockActivityId,
      "activity_name": "durationboi",
      "continuous": false,
      "start_time": "2020-08-05T11:11:00+1200",
      "end_time": "2020-08-07T11:11:00+1200",
      "description": "awdawdadw",
      "location": "awd",
      "creator_id": mockProfileId,
      "outcomes": [],
      "num_followers": 15,
      "num_participants": 0,
      "activity_type": ["Running"]
    }
  )
  getMyUserId.mockResolvedValue(mockProfileId);
  followActivity.mockResolvedValue(null);
  unfollowActivity.mockResolvedValue(null);


  beforeEach(mountFunction)

  function mountFunction() {
    let vuetify = new Vuetify()
      wrapper = mount(Activity, {
        localVue,
        vuetify,
        mocks,
        sync: false,
        stubs
    })
  }

  it('should say unfollow if we are following the activity', async () => {
    getIsFollowingActivity.mockResolvedValue(true)
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick() // resolves getActivity
    await Vue.nextTick() // resolves getIsFollowingActivity
    expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("unfollow")
  })

  it('should say follow if we are not following the activity', async () => {
    getIsFollowingActivity.mockResolvedValue(false)
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick() // resolves getActivity
    await Vue.nextTick() // resolves getIsFollowingActivity
    expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("follow")
  })

  it('clicking on follow as a non-following user sends a follow request and updates the follow button text to Unfollow', async () => {
    getIsFollowingActivity.mockResolvedValue(false)
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick() // resolves getActivity
    await Vue.nextTick() // resolves getIsFollowingActivity
    let followButton = wrapper.find('#followBtn')
    followButton.trigger('click')
    await Vue.nextTick() // wait for click
    expect(followActivity.mock.calls.length).toBe(1)
    expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("unfollow")
  })

  it('clicking on unfollow as a following user sends an unfollow request and updates the follow button text to Follow', async () => {
    getIsFollowingActivity.mockResolvedValue(true)
    mountFunction(); // need to re-mount after changing mock resolve
    await Vue.nextTick() // resolves getActivity
    await Vue.nextTick() // resolves getIsFollowingActivity
    let followButton = wrapper.find('#followBtn')
    followButton.trigger('click')
    await Vue.nextTick() // wait for click
    expect(unfollowActivity.mock.calls.length).toBe(1)
    expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("follow")
  })

})