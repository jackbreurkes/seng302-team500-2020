import Vue from "vue";
import App from "./App.vue";
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import Homepage from "./components/Homepage.vue";
import EditProfile from "./components/EditProfile.vue";
import CreateActivity from "./components/CreateActivity.vue";

Vue.config.productionTip = false;

import VueLogger from "vuejs-logger";
import VueRouter, { Route } from "vue-router";
import { verifyUserId } from "./models/user.model";
import vuetify from "./plugins/vuetify";

const routes = [
  { path: "/", name: "login", component: Login },
  { path: "/login", name: "login", component: Login },
  { path: "/register", name: "register", component: Register },
  {
    path: "/createActivity",
    name: "createActivity",
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
];

const router = new VueRouter({
  routes: routes,
  mode: "history",
});

// all routes except those given as unprotected routes should require login
const unprotectedRoutes = ["login", "register"];
router.beforeEach((to, from, next) => {
  if (to.name && unprotectedRoutes.includes(to.name)) {
    next();
  } else {
    verifyUserId()
      .then(() => {
        next();
      })
      .catch(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        next({ name: "login" });
      });
  }
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
