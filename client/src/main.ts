import Vue from 'vue'
import App from './App.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Homepage from './components/Homepage.vue'

Vue.config.productionTip = false

import VueLogger from 'vuejs-logger';
import VueRouter, { Route } from 'vue-router';
import { verifyUserId } from './models/user.model'
import vuetify from './plugins/vuetify';


const routes = [
  {path: '/', name: 'login', component: Login},
  {path: '/login', name: 'login', component: Login},
  {path: '/register', name: 'register', component: Register},
  {
    path: '/profiles/:profileId',
    name: 'profilePage',
    component: Homepage,
    beforeEnter(to: Route, from: Route, next: any) {
      verifyUserId()
      .then(() => {next()})
      .catch(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        next({ name: 'login' });
      })
    }
  },
];

const router = new VueRouter({
  routes: routes,
  mode: 'history'
})

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

// @ts-ignore required to allow VueLogger to be recognised
Vue.use(VueLogger, options);
Vue.use(VueRouter);

/* eslint-disable no-new */
new Vue({
  router,
  el: '#app',
  template: '<App/>',
  vuetify,
  components: { App }
});
