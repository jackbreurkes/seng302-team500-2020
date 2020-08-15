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