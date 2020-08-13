import * as homefeedModel from '../models/homefeed.model';
import { HomeFeedCardType } from '../scripts/HomeFeedCardType';

/**
 * Requests home feed data and returns it in a list of objects for controller
 * @param userId of the homefeed being requested
 */
export async function getHomeFeedData(){
    let homefeedData = await homefeedModel.getUsersHomefeed();
    let processed_home_feed = []
    console.log("BBBBBBBBBBBB")
    homefeedData.forEach(function(data : JSON){
        console.log("AAAAAAAAAAAAAAAA");
        console.log(data);
    });
}