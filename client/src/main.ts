import Vue from "vue";
import App from "./App.vue";
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import Homepage from "./components/Homepage.vue";
import EditProfile from "./components/EditProfile.vue";
import CreateActivity from "./components/CreateActivity.vue";
import AdminDashboard from "./components/AdminDashboard.vue";
import EditActivity from "./components/EditActivity.vue";

Vue.config.productionTip = false;

import VueLogger from "vuejs-logger";
import VueRouter, { Route } from "vue-router";
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
    next();
    return;
  }
  
  if (auth.getMyToken() === null) { // user is not authenticated
    next({ name: "login" });
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
