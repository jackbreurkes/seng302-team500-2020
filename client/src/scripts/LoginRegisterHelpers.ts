export function tagMandatoryAttributes(refs: { [x: string]: any; }, attributes: any) {
    for (let attribute of attributes) {
      let field = refs[attribute];
      field.setAttribute("class", "mandatory");
      field.required = true;
    }
  }


export function isValidEmail(email: string) {
  // RegEx taken from https://emailregex.com/
  // eslint-disable-next-line no-useless-escape
  var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
}

export function hasNumber(myString: string) {
  return /\d/.test(myString);
}

export function hasWhiteSpace(myString: string) {
  return /\s/g.test(myString);
}