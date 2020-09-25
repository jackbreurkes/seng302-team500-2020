import axios from "axios";
import * as auth from "./auth.service";
import log from "./logger.service";

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 10000,
});

// external API urls are defined here so that the interceptor can handle them accordingly if necessary.
export const externalApiRootUrls = {
    restCountries: "https://restcountries.eu/rest/v2", // CORS policy blocks X-Auth-Token header
    nominatim: "https://nominatim.openstreetmap.org",
}

/**
 * adds token to all requests sent with axios.
 * including the token has no effect on requests to our database where token not required.
 * requests to third party applications may require a separate instance, depending on CORS policy regarding unknown headers.
 */
instance.interceptors.request.use(function(config) {
  const token = auth.getMyToken();
  if (config.url && config.url.startsWith(externalApiRootUrls.restCountries)) {
      return config;
  }

  config.headers["X-Auth-Token"] = token;
  return config;
});

/**
 * on error, takes an error thrown by axios and modifies the message property to be descriptive.
 * does not alter the other fields of the error object.
 * @param e the error thrown by axios
 * @returns the error object with a modified message property
 */
instance.interceptors.response.use(
  function(response) {
    return response;
  },
  function(error) {
    if (error.response && error.response.status === 401) {
      auth.clearAuthInfo();
    }
    log(error, "error");
    error.message = axiosErrorToMessage(error);
    return Promise.reject(error);
  }
);

/**
 * takes an error thrown by axios and returns a reader-friendly message.
 * @param e the error thrown by axios
 * @returns a reader-friendly message explaining the error
 */
function axiosErrorToMessage(e: any) {
  if (e.response) {
    // a request was made and the server responded
    return e.response.data.error;
  } else if (e.request) {
    return "unable to complete request";
  } else {
    // something happened in setting up the request
    return "unknown error - check console for more info";
  }
}

export default instance;
