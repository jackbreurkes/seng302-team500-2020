import Vue from 'vue'
import App from './App.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Homepage from './components/Homepage.vue'
import InitialPage from './InitialPage.vue'

Vue.config.productionTip = false

import VueLogger from 'vuejs-logger';
import VueRouter from 'vue-router';


const routes = [
  {path: '/', name: 'initialPage', component: InitialPage},
  {path: '/login', name: 'login', component: Login},
  {path: '/register', name: 'register', component: Register},
  {path: '/myprofile', name: 'profilePage', component: Homepage,
  beforeEnter(to, from, next) {
    try {
      JSON.parse(localStorage.currentUser);
      next()
    } catch {
      next({ name: 'login' });
    }
  }},
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

// Vue.use(VueLogger, options); TODO reimplement
Vue.use(VueRouter);

/* eslint-disable no-new */
new Vue({
  router,
  el: '#app',
  template: '<App/>',
  components: { App }
});