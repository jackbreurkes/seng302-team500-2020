import instance from "../services/axios.service";
import { CreateActivityRequest } from "@/scripts/Activity";

/**
 * sends a GET request to <api base url>/activities to search for activities.
 * @param termsToSend the list of strings that will be used to search activities
 * @param pageNumber the page number desired
 * @param pageSize the size of the pages being used
 * @returns the list of activities that were matched by the search
 */
export async function searchAppActivities(
  termsToSend: string[],
  pageNumber: number,
  pageSize: number
): Promise<CreateActivityRequest[]> {
  let res = await instance.get("/activities", {
    params: {
      searchTerms: termsToSend.join(","),
      page: pageNumber,
      pageSize: pageSize
    }
  });
  return res.data;
}
