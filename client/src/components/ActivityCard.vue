<template>
    <v-card class="mb-4" @click="goToActivity(activityId)">
        <v-card-title>
            {{ activityName }} ({{ activityLocation }})
            <v-spacer></v-spacer>
            <v-chip class="ml-2" @click="editActivity(`${activityId}`)" v-if="hasAuthority">Edit</v-chip>
            <v-tooltip top>
                <template v-slot:activator="{ on }">
                <div v-on="on">
                    <v-chip class="ml-2" id="creatorChip" v-if="myProfileId === creatorId" outlined>Creator</v-chip>
                </div>
                </template>
                <span>Your role</span>
            </v-tooltip>
            <v-chip class="ml-2" id="organiserChip" v-if="organiser" outlined>Organiser</v-chip>
            <v-chip class="ml-2" id="followerChip" v-if="following" outlined>Following</v-chip>
            <v-chip class="ml-2" id="participantChip" v-if="participating" outlined>Participating</v-chip>
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
            following: false,
            participating: false,
            organiser: false,
            myProfileId: NaN as number,
        }
    },

    async created() {
        const myProfileId = authService.getMyUserId();
        if (myProfileId === null) {
            return
        }
        this.myProfileId = myProfileId;
        activityController.getIsFollowingActivity(this.myProfileId, this.activityId)
        .then((following) => {
            this.following = following;
        })
        activityController.getIsParticipating(this.myProfileId, this.activityId)
        .then((participating) => {
            this.participating = participating;
        })
        activityController.getIsOrganising(this.myProfileId, this.activityId)
        .then((organiser) => {
            this.organiser = organiser;
        } )
    },

    methods: {
        getDurationDescription(startTime: string, endTime: string): string {
            return "Starts: " + activityController.describeDate(startTime)
            + " Ends: " + activityController.describeDate(endTime);
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
