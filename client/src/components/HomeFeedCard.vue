<template>
  <div id="HomeFeedCard">
    <v-layout justify-center class="pt-1">
        <v-card width='600' height='150'>
            <v-toolbar @click=activityClicked color="blue" dark flat height='50'>
                <v-card-title>{{activityName}}</v-card-title>
            </v-toolbar>
            <v-toolbar-title class="pl-7 pt-3" @click=creatorClicked>Created by {{creatorName}}</v-toolbar-title>
                <v-spacer></v-spacer>
            <v-card-text class="pl-7">{{userActionName}}, performed {{userAction}} on {{userActionTimeStamp}}</v-card-text>
        </v-card>
    </v-layout>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'

const HomeFeedCard = Vue.extend({
    name: "HomeFeedCard",
    props: ['cardData'],
    data: function(){
        return{
            activityName: this.cardData.entity_name,
            creatorName: this.cardData.creator_name,
            userActionName: this.cardData.editor_action,
            userAction: this.cardData.editor_action,
            userActionTimeStamp: "01/01/1970; 24:70",
            entity_id: this.cardData.entity_id,
            creator_id: this.cardData.creator_id
        };
    },
    created(){

    },

    methods: {
        activityClicked: function(){
            this.$router.push("/profiles/" + this.creator_id + "/activities/" + this.entity_id)
        },
        creatorClicked: function(){
            this.$router.push("/profiles/" + this.creator_id)
        }
    }
})
export default HomeFeedCard;
</script>