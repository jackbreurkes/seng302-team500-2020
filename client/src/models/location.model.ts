import instance, { externalApiRootUrls } from "../services/axios.service";

export async function checkCountryExistence(location: string) {
  let res = await instance.get(externalApiRootUrls.nominatim + `/search/?q=${location}&format=json&addressdetails=1&accept-language=en`);
  return res.data;
}

export async function getAddressFormattedString(location: string) {
let res = await instance.get(externalApiRootUrls.nominatim + `/search/?q=${location}&format=json&addressdetails=1&accept-language=en&limit=1`);
return res.data;
}