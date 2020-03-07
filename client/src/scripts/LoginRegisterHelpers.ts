export function tagMandatoryAttributes(refs: { [x: string]: any; }, attributes: any) {
    for (let attribute of attributes) {
      let field = refs[attribute];
      field.setAttribute("class", "mandatory");
      field.required = true;
    }
  }
