<template>
  <div id="HomeFeedCard">
    <v-layout justify-center class="pt-1">
        <v-card width='600' height='100%'>
            <v-toolbar  :color="suggestion ? 'green' : 'blue'" dark flat height='50'>
                <v-icon class="mr-2">mdi-account-edit</v-icon>
                <v-toolbar-title >{{activityName}}</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-menu bottom left offset-y>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        dark
                        icon
                        v-bind="attrs"
                        v-on="on"
                        v-if="!suggestion"
                      >
                        <v-icon >mdi-dots-vertical</v-icon>
                      </v-btn>
                    </template>

                    <v-list>
                      <v-list-item
                        id="unsubscribe"
                        @click="unsubscribe"
                      >
                        <v-list-item-title>Stop following</v-list-item-title>
                      </v-list-item>
                    </v-list>
                  </v-menu>
            </v-toolbar>
            <v-card-text class="pl-7 pt-3" v-if="creatorName">Activity created by <router-link :to="'/profiles/' + this.creatorId"><p>{{creatorName}}</p></router-link></v-card-text>
            <v-spacer></v-spacer>
            <v-card-text class="pl-7 subtitle-1">{{infoString}}<router-link :to="'/profiles/' + this.creatorId + '/activities/' + this.entityId"><p class="ml-1">Go to activity</p></router-link></v-card-text>
        </v-card>
    </v-layout>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import {unfollowActivity} from '../controllers/activity.controller';
import * as authService from '../services/auth.service';
// eslint-disable-next-line no-unused-vars
import { CreateActivityRequest } from '../scripts/Activity';
import { getActivity } from '../controllers/activity.controller'

const HomeFeedCard = Vue.extend({
    name: "HomeFeedCard",
    props: ['cardData'],
    data: function(){
        return{
            activityName: this.cardData.entity_name,
            creatorName: this.cardData.creator_name,
            editorName: this.cardData.editorName,
            editorAction: "edited",
            userActionTimeStamp: this.cardData.edited_timestamp || null,
            entityId: this.cardData.entity_id,
            creatorId: this.cardData.creator_id,
            oldValue: "",
            newValue: "",
            infoString: "",
            suggestion: false,
            activity: [] as CreateActivityRequest,
        };
    },
    async created(){
        this.activity = await getActivity(this.cardData.creator_id, this.cardData.entity_id);
        this.parseChangelogResponse();
    },

    methods: {
        parseTime: function(time:string){
            let dateTime = new Date(time);
            const dtf = new Intl.DateTimeFormat(undefined, {
                year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric', timeZoneName: 'short'});
            return dtf.format(dateTime)
        },
        parseEditorAction: function(){
            if(this.cardData.changed_attribute == "ACTIVITY_EXISTENCE"){
                if(this.cardData.action_type === "CREATED"){
                    return "created the activity " + this.cardData.entity_name;
                }else {
                    return "deleted the activity " + this.cardData.entity_name;
                }
                
            } else if (this.cardData.changed_attribute == "ACTIVITY_OUTCOME") {
                if (this.cardData.action_type === "CREATED") {
                    return "has created an activity outcome '" + this.cardData.new_value.description + "' where you can log your results. Changed"
                } else {
                    return "has removed the activity outcome '" + this.cardData.old_value.description + "'. Changed"
                }
            } else if (this.cardData.changed_attribute == "ACTIVITY_RESULT") {
                if (this.cardData.action_type === "CREATED" || this.cardData.action_type === "UPDATED") {
                    return "has logged a result of '" + this.cardData.new_value.value + " " + this.cardData.new_value.outcome_unit + "' to '" + this.cardData.new_value.outcome_description + "'. Changed"  
                } else {
                    return "has removed their result of '" + this.cardData.old_value.value + " " + this.cardData.old_value.outcome_unit + "' from '" + this.cardData.old_value.outcome_description + "'. Changed" 
                }
            }
             else if(this.cardData.changed_attribute == "ACTIVITY_NAME"){
                return "updated the activity name from \"" + this.cardData.old_value + "\" to \"" + this.cardData.new_value + "\"";
            } else if(this.cardData.changed_attribute == "ACTIVITY_DESCRIPTION"){
                return "updated the activity description from \"" + this.cardData.old_value + "\" to \"" + this.cardData.new_value + "\"";
            } else if(this.cardData.changed_attribute == "ACTIVITY_TIME_FRAME"){
                if(this.cardData.old_value == null){
                    //A timeframe has been added where there was none
                    return "added a time to the activity, now running from " + this.parseTime(this.cardData.new_value.start_time) + " to " +this.parseTime(this.cardData.new_value.end_time) + ". Changed"
                } else if (this.cardData.new_value === null) {
                    return "has updated the activity's time from " + this.parseTime(this.cardData.old_value.start_time) + " to a continuous activity. Changed"
                } else {
                    return "updated the activities time. Used to run from " + this.parseTime(this.cardData.old_value.start_time) + " to " + this.parseTime(this.cardData.old_value.end_time) + ". Now runs from "
                     + this.parseTime(this.cardData.new_value.start_time) + " to " +this.parseTime(this.cardData.new_value.end_time) + ". Changed";
                }
            } else if(this.cardData.changed_attribute == "ACTIVITY_LOCATION"){
                return "updated the activity location from " + this.cardData.old_value + " to " + this.cardData.new_value;
            } else if(this.cardData.changed_attribute == "ACTIVITY_ACTIVITY_TYPES"){
                var activityTypesString =  "updated the activities activity types from ";
                this.cardData.old_value.forEach((element:string) => {
                    activityTypesString += element + ", "
                });
                activityTypesString = activityTypesString.substring(0, activityTypesString.length - 2) + " to "
                this.cardData.new_value.forEach((element:string) => {
                    activityTypesString += element + ", ";
                });
                return activityTypesString.substring(0, activityTypesString.length - 2);
            } else if (this.cardData.changed_attribute == "RECOMMENDED_ACTIVITY"){
                let actName = this.activityName;
                this.activityName = "Recommended: " + actName;
                this.suggestion = true;
                return "Suggested because it is in " + this.activity.location + " and you are interested in at least one of " + this.activity.activity_type;
            }
            return this.cardData.changed_attribute;
        },
        parseChangelogResponse: function(){
            let string = this.parseEditorAction();
            
            if(!this.suggestion) {
                this.infoString = this.cardData.editor_name + " " + string + " on " + this.parseTime(this.cardData.edited_timestamp);
            } else {
                this.infoString = string;
            }
        },
        unsubscribe: async function() {
            let myProfileId = await authService.getMyUserId();
            if (myProfileId === undefined || myProfileId === null) {
                return;
            }
            try {
                await unfollowActivity(myProfileId, this.entityId);
            } catch (err) {
                if (!(err.response && err.response.status === 404)) { // ignore 404s
                    throw err;
                } else if (!(err.message && err.message.toLowerCase().startsWith("user not subscribed"))) { // if the 404 has the wrong message
                    throw err;
                }
            }
            this.creatorName = null;
            this.activityName = "Unfollowed"
            this.infoString = "You have unfollowed this activity. You will no longer receive updates about this activity."
            this.$root.$emit('refreshPins');
        }
    }
})
export default HomeFeedCard;
</script>