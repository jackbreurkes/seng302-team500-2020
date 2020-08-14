<template>
    <v-card class="mb-4" @click="goToActivity(activityId)">
        <v-card-title>
            {{ activityName }} ({{ activityLocation }})
            <v-spacer></v-spacer>
            <v-chip class="ml-2" @click="editActivity(`${activityId}`)" v-if="hasAuthority">Edit</v-chip>
            <v-tooltip top>
                <template v-slot:activator="{ on }">
                <div v-on="on">
                    <v-chip class="ml-2" outlined> {{ userRole }} </v-chip>
                </div>
                </template>
                <span>Your role</span>
            </v-tooltip>
        </v-card-title>
        <v-card-text>
            <span class="subtitle-1">{{activityDescription}}</span>
            <br/>
            <p class="body-2"
                > {{ isContinuous ? 'continuous' : getDurationDescription(activityStartTime, activityEndTime) }}
            </p>
            <v-spacer></v-spacer>
            <v-chip
            class="mr-2 mb-2"
            v-for="type of activityType"
            v-bind:key="type"
            outlined
            >{{ type }}</v-chip>
        </v-card-text>
    </v-card>
</template>


<script lang="ts">
import Vue from "vue";
import * as activityController from "../controllers/activity.controller";
import * as authService from "../services/auth.service";

const ActivityCard = Vue.extend({
    name: "ActivityCard",
    props: ["activityName", "activityLocation", "activityDescription", "isContinuous", "activityStartTime", "activityEndTime", "activityType", "activityId", "creatorId", "authority"],

    data: function() {
        return {
            hasAuthority: this.authority as boolean,
            userRole: "Not Associated",
            following: false,
            participating: false,
            myProfileId: NaN as number,
        }
    },

    created() {
        this.myProfileId = authService.getMyUserId();
        activityController.getIsFollowingActivity(this.myProfileId, this.activityId)
        .then((following) => {
            this.following = following;
        })
        activityController.getIsParticipating(this.myProfileId, this.activityId)
        .then((participating) => {
            this.participating = participating;
        })
        if(this.myProfileId === this.creatorId) {
            this.userRole = "Creator";
        } else if (this.following) {
            this.userRole = "Following";
        } else if (this.participating) {
            this.userRole = "Participating";
        }
    },

    methods: {
        getDurationDescription(startTime: string, endTime: string): string {
            return activityController.describeDurationTimeFrame(startTime, endTime);
        },
        editActivity(activityId: number) {
            this.$router.push(
                `/profiles/${this.creatorId}/editActivity/${activityId}`
            );
        },
        goToActivity(activityId: number) {
            this.$router.push(
                `/profiles/${this.creatorId}/activities/${activityId}`
            );
        }
    }
});

export default ActivityCard;
</script>

<style>
[v-cloak] {
  display: none;
}
p {
  display: inline-block;
}
</style>
