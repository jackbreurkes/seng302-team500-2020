import Vue from "vue";
import App from "./App.vue";
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import Homepage from "./components/Homepage.vue";
import EditProfile from "./components/EditProfile.vue";
import CreateActivity from "./components/CreateActivity.vue";
import AdminDashboard from "./components/AdminDashboard.vue";
import Search from './components/Search.vue';
import HomeFeed from './components/HomeFeed.vue';
import Activity from './components/Activity.vue';
import ActivitySearch from './components/ActivitySearch.vue';

Vue.config.productionTip = false;

import VueLogger from "vuejs-logger";
import VueRouter from "vue-router";
import * as auth from "./services/auth.service";
import vuetify from "./plugins/vuetify";

const ROUTER_BASE_URL = process.env.VUE_APP_BASE_URL;

const routes = [
  { path: "/", name: "landing", component: Login },
  { path: "/login", name: "login", component: Login },
  { path: "/register", name: "register", component: Register },
  {
    path: "/profiles/:profileId/createActivity",
    name: "createActivity",
    component: CreateActivity,
  },
  {
    path: "/profiles/:profileId/editActivity/:activityId",
    name: "editActivity",
    component: CreateActivity,
  },
  {
    path: "/profiles/:profileId",
    name: "profilePage",
    component: Homepage,
  },
  {
    path: "/profiles/:profileId/edit",
    name: "editProfile",
    component: EditProfile,
  },
  {
    path: "/admin",
    name: "adminDashboard",
    component: AdminDashboard,
  },
  {
    path: "/search",
    name: "search",
    component: Search,
  },
  {
    path: "/profiles/:profileId/activities/:activityId",
    name: "activity",
    component: Activity,
  },
  {
    path: "/homefeed",
    name: "homefeed",
    component: HomeFeed,
  },
  {path: "/activities", name: "activities", component: ActivitySearch}
];

const router = new VueRouter({
  routes: routes,
  base: ROUTER_BASE_URL,
  mode: "history",
});

// all routes except those given as unprotected routes should require login
const unprotectedRoutes = ["login", "register"];
router.beforeEach((to, from, next) => {
  if (to.name && unprotectedRoutes.includes(to.name)) {
    if (auth.getMyToken() === null) { //user is not authenticated
      next();
      return;
    } else {
      next({name: "homefeed" });
      return;
    }
  }
  
  if (auth.getMyToken() === null) { // user is not authenticated
    next({ name: "login" });
    return;
  }

  //checking if route is going to admin dashboard, and if it is and 
  //the permission level is too low then they will be redirected
  if(to.name === "adminDashboard" && !auth.isAdmin()) {
    next({name: "homefeed"});
    return;
  }

  //checks if the user is going to an edit profile page which is not their own
  //and if so redirects to homefeed
  if(to.name === "editProfile" && !auth.isAdmin() && parseInt(to.params.profileId) !== auth.getMyUserId()){
    next({name: "homefeed"});
    return;
  }

  //checks if a user is trying to go to a unnamed route and if so
  //they are redirected to the homefeed
  if(to.name === null){
    next({name: "homefeed"});
    return;
  }

  next();
});

const options = {
  isEnabled: true,
  logLevel: "debug",
  stringifyArguments: false,
  showLogLevel: true,
  showMethodName: false,
  separator: "|",
  showConsoleColors: true,
};

// @ts-ignore required to allow VueLogger to be recognised
Vue.use(VueLogger, options);
Vue.use(VueRouter);

/* eslint-disable no-new */
new Vue({
  router,
  el: "#app",
  template: "<App/>",
  vuetify,
  components: { App },
});
