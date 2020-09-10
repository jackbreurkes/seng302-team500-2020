package com.springvuegradle.util;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Override
    public void initialize(EmailConstraint emailConstraint) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // check if there is exactly 1 '@'
        if (value.indexOf("@") != -1 && value.indexOf("@") == value.lastIndexOf("@")) {
            String[] components = value.split(Pattern.quote("@"));

            if (components[0].length() == 0 || components[0].length() > 64) {
                return false;
            }

            if (components[1].length() == 0 || components[1].length() > 128) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }
}
