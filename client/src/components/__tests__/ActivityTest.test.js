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
    getActivity,
    getIsFollowingActivity,
    followActivity,
    unfollowActivity,
    getIsParticipating,
    participateInActivity,
    removeActivityRole
} from "../../controllers/activity.controller";
jest.mock("../../controllers/activity.controller.ts")
import {isAdmin, getMyUserId} from "../../services/auth.service";
jest.mock("../../services/auth.service")

Vue.use(Vuetify)
const localVue = createLocalVue()
const mockProfileId = 7001;
const mockActivityCreatorId = 7002;
const mockActivityId = 5;

const mocks = { //mock route
    $route: {
        params: {
            profileId: mockActivityCreatorId,
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
    getActivity.mockResolvedValue({
        "activity_id": mockActivityId,
        "activity_name": "durationboi",
        "continuous": false,
        "start_time": "2020-08-05T11:11:00+1200",
        "end_time": "2020-08-07T11:11:00+1200",
        "description": "awdawdadw",
        "location": "awd",
        "creator_id": mockActivityCreatorId,
        "outcomes": [],
        "num_followers": 15,
        "num_participants": 0,
        "activity_type": ["Running"]
    })
    getMyUserId.mockImplementation(() => mockProfileId);
    followActivity.mockResolvedValue(null);
    unfollowActivity.mockResolvedValue(null);
    participateInActivity.mockResolvedValue(null);
    removeActivityRole.mockResolvedValue(null);


    beforeEach(mountFunction)

    afterEach(() => {
        jest.clearAllMocks();
    })

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

    it('should say unfollow if we are following the activity', async() => {
        getIsFollowingActivity.mockResolvedValue(true)
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("unfollow")
    })

    it('should say follow if we are not following the activity', async() => {
        getIsFollowingActivity.mockResolvedValue(false)
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("follow")
    })

    it('clicking on follow as a non-following user sends a follow request and updates the follow button text to Unfollow', async() => {
        getIsFollowingActivity.mockResolvedValue(false)
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        let followButton = wrapper.find('#followBtn')
        followButton.trigger('click')
        await Vue.nextTick() // wait for click
        await Vue.nextTick() // wait for click
        expect(followActivity.mock.calls.length).toBe(1)
        expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("unfollow")
    })

    it('clicking on unfollow as a following user sends an unfollow request and updates the follow button text to Follow', async() => {
        getIsFollowingActivity.mockResolvedValue(true)
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        let followButton = wrapper.find('#followBtn')
        followButton.trigger('click')
        await Vue.nextTick() // wait for click
        await Vue.nextTick() // wait for click
        expect(unfollowActivity.mock.calls.length).toBe(1)
        expect(wrapper.find('#followBtn').text().toLowerCase()).toBe("follow")
    })

    it('should say participate if we not a participant', async() => {
        getIsParticipating.mockResolvedValue(false);
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        expect(wrapper.find('#participateBtn').text().toLowerCase()).toBe("participate")
    })

    it('should say unparticipate if we are a participant', async() => {
        getIsParticipating.mockResolvedValue(true);
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        expect(wrapper.find('#participateBtn').text().toLowerCase()).toBe("unparticipate")
    })

    it('should remove us as a participant if we unparticipate', async() => {
        getIsParticipating.mockResolvedValue(true);
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        wrapper.find('#participateBtn').trigger('click')
        await Vue.nextTick()

        expect(removeActivityRole.mock.calls.length).toBe(1)
        expect(removeActivityRole.mock.calls[0][0]).toBe(mockProfileId);
        expect(removeActivityRole.mock.calls[0][1]).toBe(mockActivityId);
    })

    it('should add us as a participant if we participate', async() => {
        getIsParticipating.mockResolvedValue(false);
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        wrapper.find('#participateBtn').trigger('click')
        await Vue.nextTick()

        expect(participateInActivity.mock.calls.length).toBe(1)
        expect(participateInActivity.mock.calls[0][0]).toBe(mockProfileId);
        expect(participateInActivity.mock.calls[0][1]).toBe(mockActivityId);
    })

    it('shouldnt try to change participant status if we created the activity', async() => {
        getIsParticipating.mockResolvedValue(false);
        getMyUserId.mockImplementation(() => mockActivityCreatorId);
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        await Vue.nextTick() // resolves getIsFollowingActivity
        wrapper.find('#participateBtn').trigger('click')
        await Vue.nextTick()

        expect(participateInActivity.mock.calls.length).toBe(0)
    })

})

describe("testEditButton", () => {

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
    getActivity.mockResolvedValue({
        "activity_id": mockActivityId,
        "activity_name": "durationboi",
        "continuous": false,
        "start_time": "2020-08-05T11:11:00+1200",
        "end_time": "2020-08-07T11:11:00+1200",
        "description": "awdawdadw",
        "location": "awd",
        "creator_id": mockActivityCreatorId,
        "outcomes": [],
        "num_followers": 15,
        "num_participants": 0,
        "activity_type": ["Running"]
    })


    beforeEach(() => {
        getMyUserId.mockImplementation(() => mockProfileId);
    })

    afterEach(() => {
        jest.clearAllMocks();
    })

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

    it('If we are logged in, not as creator/organister or admin we shouldnt see an edit button', async() => {
        isAdmin.mockImplementation(() => false)
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        expect(wrapper.find('#editBurger').exists()).toBe(false)
    })

    it('If we are logged in as an admin, we should see an edit button for activity', async() => {
        isAdmin.mockImplementation(() => true)
        mountFunction(); // need to re-mount after changing mock resolve
        await Vue.nextTick() // resolves getActivity
        expect(wrapper.find('#editBurger').exists()).toBe(true)
    })
})