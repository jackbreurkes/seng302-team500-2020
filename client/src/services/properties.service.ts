export function getAdminMode() {
    return getCookie("adminMode")=="true";
}

export function setAdminMode(on: boolean) {
    if (on) {
        document.cookie = "adminMode=true";
      } else {
        document.cookie = "adminMode=false";
      }
}

export function removeAdminCookie() {
  document.cookie = "adminMode=; expires=Thu, 01 Jan 1970 00:00:01 GMT;"; // Will remove the adminMode cookie if exists
}

export function getCookie(name: string) {
    let value = `; ${document.cookie}`;
    let parts = value.split(`; ${name}=`);
    if (parts && parts.length === 2) {
      let cookieEnd = parts.pop();
      if (cookieEnd !== undefined) {
        return cookieEnd.split(';').shift()
      }
    }
    return null;
  }