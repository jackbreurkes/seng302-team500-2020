import instance, { externalApiRootUrls } from "../services/axios.service";

export async function checkCountryExistence(location: string) {
    // TODO get this to poll our database instead of the API?
  let res = await instance.get(externalApiRootUrls.nominatim + `/search/?q=${location}&format=json&addressdetails=1&accept-language=en`);
  return res.data;
}