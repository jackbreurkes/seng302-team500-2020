import axios from "axios";

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
  timeout: 10000,
});

export async function getActivityTypes() {
  let res;
  try {
    let res = await instance.get(SERVER_URL+"/activity-types");
    return res.data;
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
}
/*
try { // TODO there should be no business logic in the model class
    let user = await getProfileById(profileId);
    let emails = user.additional_email;
    if (emails === undefined) {
      emails = [email];
    } else {
      emails.push(email);
    }
    let emailDict = {"additional_email": emails, "email": email}
    let res = await instance.post("profiles/" + profileId + "/emails", emailDict, {
      headers: {
        "X-Auth-Token": localStorage.getItem("token")
      }, data: emailDict
    });
  } catch (e) {
    throw new Error(e.response.data.error)
  }*/