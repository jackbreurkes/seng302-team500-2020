<template>
  <div id="HomeFeedCard">
    <v-layout justify-center class="pt-1">
        <v-card width='600' height='400'>
            <v-toolbar color="red" dark flat height='80'>
                <div> 
                    <v-toolbar-title >Created by {{creatorName}} on {{createdTimestamp}}  </v-toolbar-title>
                </div>
                <div >
                    <!-- <v-card-title >{{activityName}}</v-card-title> -->
                </div>

            </v-toolbar>
            <v-card-text >AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaa</v-card-text>
        </v-card>
    </v-layout>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'

const HomeFeedCard = Vue.extend({
    name: "HomeFeedCard",
    data: function(){
        return{
            activityName: "Activity Name",
            creatorName: "Creator Name",
            createdTimestamp: "11/06/1000 @ 10pm",
            userRole: "Role",
            userAction: "Action",
            userActionTimeStamp: "12.44.2222.333.4444"   
        };
    },
    created(){
        //todo, get data to populate card
    },

    methods: {

    }
})
export default HomeFeedCard;
</script>