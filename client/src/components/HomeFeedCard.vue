<template>
  <div id="HomeFeedCard">
    <v-layout justify-center class="pt-1">
        <v-card width='600' height='100%'>
            <v-toolbar @click=activityClicked color="blue" dark flat height='50'>
                <v-card-title>{{activityName}}</v-card-title>
            </v-toolbar>
            <v-card-text class="pl-7 pt-3 " @click=creatorClicked>Created by {{creatorName}}</v-card-text>
                <v-spacer></v-spacer>
            <v-card-text class="pl-7 subtitle-1">{{infoString}}</v-card-text>
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
            editorName: this.cardData.editorName,
            editorAction: "edited",
            userActionTimeStamp: this.cardData.edited_timeStamp,
            entityId: this.cardData.entity_id,
            creatorId: this.cardData.creator_id,
            oldValue: "",
            newValue: "",
            infoString: ""
        };
    },
    created(){
        this.parseChangelogResponse();
        
    },

    methods: {
        activityClicked: function(){
            this.$router.push("/profiles/" + this.creatorId + "/activities/" + this.entityId)
        },
        creatorClicked: function(){
            this.$router.push("/profiles/" + this.creatorId)
        },
        parseTime: function(time:string){
            let dateTime = new Date(time);
            const dtf = new Intl.DateTimeFormat(undefined, {
                year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric', timeZoneName: 'short'});
            return dtf.format(dateTime)
        },
        parseEditorAction: function(){
            if(this.cardData.changed_attribute == "ACTIVITY_EXISTENCE"){
                return "created the activity " + this.cardData.entity_name;
            }else if(this.cardData.changed_attribute == "ACTIVITY_NAME"){
                return "updated the activity name from \"" + this.cardData.old_value + "\" to \"" + this.cardData.new_value + "\"";
            }else if(this.cardData.changed_attribute == "ACTIVITY_DESCRIPTION"){
                return "updated the activity description from \"" + this.cardData.old_value + "\" to \"" + this.cardData.new_value + "\"";
            }else if(this.cardData.changed_attribute == "ACTIVITY_TIME_FRAME"){
                if(this.cardData.old_value == null){
                    //A timeframe has been added where there was none
                    return "added a time to the activity, now running from " + this.parseTime(this.cardData.new_value.start_time) + " to " +this.parseTime(this.cardData.new_value.end_time) + ". Changed"
                }else{
                    return "updated the activities time. Used to run from " + this.parseTime(this.cardData.old_value.start_time) + " to " + this.parseTime(this.cardData.old_value.end_time) + ". Now runs from "
                     + this.parseTime(this.cardData.new_value.start_time) + " to " +this.parseTime(this.cardData.new_value.end_time) + ". Changed";
                }
            }else if(this.cardData.changed_attribute == "ACTIVITY_LOCATION"){
                return "updated the activity location from " + this.cardData.old_value + " to " + this.cardData.new_value;
            }else if(this.cardData.changed_attribute == "ACTIVITY_ACTIVITY_TYPES"){
                var activityTypesString =  "updated the activities activity types from ";
                this.cardData.old_value.forEach((element:string) => {
                    activityTypesString += element + ", "
                });
                activityTypesString = activityTypesString.substring(0, activityTypesString.length - 2) + " to "
                this.cardData.new_value.forEach((element:string) => {
                    activityTypesString += element + ", ";
                });
                return activityTypesString.substring(0, activityTypesString.length - 2);
            }
            return this.cardData.changed_attribute;
        },
        parseChangelogResponse: function(){
            this.infoString = this.cardData.editor_name + " " + this.parseEditorAction() + " on " + this.parseTime(this.cardData.edited_timestamp);
        }
    }
})
export default HomeFeedCard;
</script>