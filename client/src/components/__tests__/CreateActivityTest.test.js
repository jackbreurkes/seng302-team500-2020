/* eslint-env jest */

import Vue from 'vue'
import Vuetify from 'vuetify'
import CreateActivity from "../CreateActivity.vue";

import {
  mount,
  createLocalVue
} from '@vue/test-utils'

import {
  getAvailableActivityTypes,
  validateNewActivity,
  editOrCreateActivity
} from "../../controllers/activity.controller";
jest.mock("../../controllers/activity.controller.ts")

import {
  createActivity
} from "../../models/activity.model";
jest.mock("../../models/activity.model.ts")

Vue.use(Vuetify)
const localVue = createLocalVue()
const mockProfileId = 7001;

const mocks = {
  $route: {
    params: {
      profileId: mockProfileId,
    }
  },
  $router: {
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

  getAvailableActivityTypes.mockResolvedValue([
    "Walking",
    "Running",
    "Swimming",
    "Dancing"
  ]);
  createActivity.mockResolvedValue(null);
  validateNewActivity.mockResolvedValue(null);
  editOrCreateActivity.mockResolvedValue(null);

  beforeEach(mountFunction)

  afterEach(() => {
    jest.clearAllMocks();
  })

  function mountFunction() {
    let vuetify = new Vuetify()
    wrapper = mount(CreateActivity, {
      localVue,
      vuetify,
      mocks,
      sync: false,
      stubs
    })
  }

  it('should send an outcome with the activity if one has been entered', async () => {
    mountFunction()
    const outcomeDescription = "outcome description"
    const outcomeUnits = "km/h"

    wrapper.find('.v-expansion-panel-header').trigger('click') // expand outcomes expantion panel
    await Vue.nextTick()
    
    wrapper.find('#outcomeDescription').setValue(outcomeDescription)
    wrapper.find('#outcomeUnits').setValue(outcomeUnits)
    await Vue.nextTick()
    wrapper.find("#createButton").trigger('click')
    await Vue.nextTick()
    const createCalls = editOrCreateActivity.mock.calls
    await Vue.nextTick()
    expect(createCalls.length).toBe(1);
    let activity = createCalls[0][0];
    expect(activity.outcomes.length).toBe(1);
    expect(activity.outcomes[0].description).toBe(outcomeDescription);
    expect(activity.outcomes[0].units).toBe(outcomeUnits);
  })

  it('should not send an outcome with the activity if one has not been entered', async () => {
    mountFunction()
    wrapper.find("#createButton").trigger('click')
    await Vue.nextTick()
    const createCalls = editOrCreateActivity.mock.calls
    await Vue.nextTick()
    expect(createCalls.length).toBe(1);
    let activity = createCalls[0][0];
    expect(activity.outcomes.length).toBe(0);
  })

})