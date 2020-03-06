<template>
  <div id="app">
    <label>Initial</label>
    <router-link to="/login">
      <p>this is login link</p>
    </router-link>
    <router-link to="/register">
      <p>this is register link</p>
    </router-link>
  </div>
</template>

<script lang="ts">
  import Register from './components/Register.vue'
  import Login from './components/Login.vue'

  
  // app Vue instance
  const app = {
    name: 'app',
    components: {
      Register,
      Login
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