import * as homefeedModel from '../models/homefeed.model';
import { HomeFeedCardType } from '../scripts/HomeFeedCardType';

/**
 * Requests home feed data and returns it in a list of objects for controller
 * @param userId of the homefeed being requested
 */
export async function getHomeFeedData(){
    let homefeedData = await homefeedModel.getUsersHomefeed();
    return homefeedData;
}

/**
 * Requests home feed suggestions and returns it in a list of objects for controller
 * @param userId of the homefeed suggestions being requested
 */
export async function getSuggestionsForHomeFeed(){
    return await homefeedModel.getSuggestedActivities();

}

/**
 * Requests additional home feed entries when infinite scrolling
 * @param lastId the id of the last changelog entry currently loaded
 */
export async function getAdditionalUsersHomefeed(lastId: number){
    let homefeedData = await homefeedModel.getAdditionalUsersHomefeed(lastId);
    return homefeedData;
}