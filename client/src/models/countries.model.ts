import instance, { externalApiRootUrls } from "../services/axios.service";

export async function loadPassportCountries() {
    // TODO get this to poll our database instead of the API?
  let res;
  try {
    res = await instance.get(externalApiRootUrls.restCountries + "/all");
  } catch (e) {
    if (e.response) {
      // request made and server responded
      console.error(e.response);
      throw new Error(e.response.data.error);
    } else if (e.request) {
      console.error(e.request);
      throw new Error(e.request);
    } else {
      // something happened in setting up the request
      console.error(e);
      throw new Error(e);
    }
  }
  return res.data;
}
