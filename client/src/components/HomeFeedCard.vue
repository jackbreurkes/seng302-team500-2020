<template>
  <div id="HomeFeedCard">
    <v-layout justify-center class="pt-1">
        <v-card width='600' height='150'>
            <v-toolbar @click=activityClicked color="blue" dark flat height='50'>
                <v-card-title>{{activityName}}</v-card-title>
            </v-toolbar>
            <v-toolbar-title class="pl-7 pt-3" @click=creatorClicked>Created by {{creatorName}} on {{createdTimestamp}}  </v-toolbar-title>
                <v-spacer></v-spacer>
            <v-card-text class="pl-7">{{userActionName}}, {{userRole}} performed {{userAction}} on {{userActionTimeStamp}}</v-card-text>
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
            createdTimestamp: "01/01/1970; 24:70",
            userActionName: "Users Name",
            userRole: "Users Role",
            userAction: "Users Action",
            userActionTimeStamp: "01/01/1970; 24:70",
            entity_id: 1,
            creator_id: 1   
        };
    },
    created(){
        this.getCardData();
    },

    methods: {
        getCardData: function(){
            //TODO 
        },
        activityClicked: function(){
            this.$router.push("/activities/" + this.entity_id)
        },
        creatorClicked: function(){
            this.$router.push("/profiles/" + this.creator_id)
        }
    }
})
export default HomeFeedCard;
</script>