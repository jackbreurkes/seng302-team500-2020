<template>
  <div id="app">
    <v-app>
      <router-view></router-view>
    </v-app>
  </div>
</template>

<script lang="ts">
  import Register from './components/Register.vue'
  import Login from './components/Login.vue'
  import Homepage from './components/Homepage.vue'
  // app Vue instance
  const app = {
    name: 'app',
    components: {
      Register,
      Login,
      Homepage,
    },
    // app initial state
    data: () => {
      return {
      }
    }
  }

  export default app
</script>

<style>
  [v-cloak] { display: none; }
</style>
