<template>
  <div id="app">
    <router-view></router-view>
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

  input {
    padding: 12px; /* Some padding */ 
    border: 1px solid #ccc; /* Gray border */
    border-radius: 4px; /* Rounded borders */
    box-sizing: border-box; /* Make sure that padding and width stays in place */
    margin-top: 6px; /* Add a top margin */
    margin-bottom: 16px; /* Bottom margin */
  }

</style>