import instance from "../services/axios.service";

/**
 * sends a GET request to <api base url>/activities to search for activities.
 * @param termsToSend the list of strings that will be used to search activities
 * @param pageNumber the page number desired
 * @param pageSize the size of the pages being used
 */
export async function searchAppActivities(termsToSend: string[], pageNumber: number, pageSize: number) {
    let res = await instance.get("/activities", {
      params: {
        searchTerms: termsToSend.join(","),
        page: pageNumber,
        pageSize: pageSize
      }
    });
    return res.data;
  }