<template>
    <div  style="text-align: center" id="Activities">
        <v-container fill-height align-content-center>
            <v-row justify="center">
                <v-col sm="12" md="12" lg="12">
                    <v-card class="elevation-12">
                        <v-toolbar color="primary" dark flat>
                            <v-toolbar-title>Search Activities</v-toolbar-title>
                        </v-toolbar>

                        <div class="mt-3 mx-2" fill-height fill-width>

                        <v-col cs="12" sm="12" md="9" lg="9">
                        <v-text-field
                            label="Activity Name"
                            outlined
                            v-model="searchString"
                            ></v-text-field>
                        </v-col>

                        <v-col md="12" class="d-flex justify-center">
                            <v-btn x-large color="primary" v-on:click="search()">Search</v-btn>
                        </v-col>

                        </div>

                            <v-row>
                                <v-col sm="2" md="2" lg="1">
                                    <v-dialog v-model="searchRulesModal" width="400">
                                        <template v-slot:activator="{ on }">
                                            <v-btn v-on="on" color="info">View Search Rules</v-btn>
                                        </template>

                                        <v-card>
                                            <v-card-title class="headline" primary-title>Search Rules</v-card-title>
                                            <v-divider></v-divider><br>
                                            <v-card-text>
                                                <p>To do a complete match search you must use quotation marks</p>
                                                <p>e.g. <b>"Swimming in lake"</b> will only match with activities with <b>Swimming in lake</b> in the name.</p>
                                                <p>To do a partial match you only need to put spaces between the words</p>
                                                <p>e.g. <b>Swimming in lake</b> will match with activities that have <b>Swimming</b>, <b>in</b> or <b>lake</b> in the name</p>
                                            </v-card-text>

                                            <v-divider></v-divider>

                                            <v-card-actions>
                                                <v-col class="text-right">
                                                    <v-btn text @click="searchRulesModal = false">Close</v-btn>
                                                </v-col>
                                            </v-card-actions>
                                        </v-card>
                                    </v-dialog>
                                </v-col>
                            </v-row>
                        <p class="pl-1" style="color: red">{{ errorMessage }}</p>
                        <v-data-table
                                :no-data-text="noDataText"
                                :headers="headers"
                                :items="users"
                                item-key="activity_id"
                                single-select
                                v-model="selectedActivities"
                        >
                            <template #name="{ item }">{{ item.activity_name }}</template>
                            <template v-slot:items="activities">
                                <td class="text-xs-right">{{ activities.item.activity_name}}</td>
                                <td class="text-xs-right">{{ activities.item.activity_type }}</td>
                                <td class="text-xs-right">{{ activities.item.creator_name }}</td>
                                <td class="text-xs-right">{{ activities.item.location }}</td>
                                <td class="text-xs-right">{{ activities.item.num_participants }}</td>
                                <td class="text-xs-right">{{ activities.item.num_followers }}</td>
                            </template>
                        </v-data-table>
                    </v-card>
                </v-col>
            </v-row>

        </v-container>
    </div>
</template>


<script lang="ts">
    import Vue from "vue";
    // eslint-disable-next-line no-unused-vars
    import {CreateActivityRequest} from "@/scripts/Activity";

    // app Vue instance
    const Activities = Vue.extend({
        name: "Activities",


        // app initial state
        data: function() {
            return {
                searchRulesModal: false,
                headers: [
                    { text: 'Name', value: 'activity_name' },
                    { text: 'Type', value: 'activity_type' },
                    { text: 'Creator', value: 'creator' },
                    { text: 'Location', value: 'location'},
                    { text: 'Participant Count', value: 'num_participants'},
                    { text: 'Follower Count', value: 'num_followers'},
                ],
                searchString: "",
                activities: [] as CreateActivityRequest[],
                selectedActivities: [] as CreateActivityRequest[],
            };
        },

        created() {

        },

        methods: {

        }

    });

    export default Activities;
</script>

<style>
    [v-cloak] {
        display: none;
    }

    .mandatory {
        background-color: #ccf;
    }

    p {
        display: inline-block;
    }

    #method-radios {
        min-width: 140px;
    }

    #activity-selector {
        min-width: 200px;
    }
</style>
