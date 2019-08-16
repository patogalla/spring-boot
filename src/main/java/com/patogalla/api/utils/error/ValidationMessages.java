package com.patogalla.api.utils.error;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class ValidationMessages {

    private ValidationMessages() {}

    public static final String VALIDATION_PROBLEM_TITLE = "There are some validation error(s) while processing the request";

    public static final String CN_INVALID_REFERENCES = "cn.all_references.not_valid";
    public static final String CN_INVALID_REFERENCE = "cn.reference.not_valid";
    public static final String CN_AMOUNT_NOT_VALID = "cn.amount.not_valid";
    public static final String CN_POSITIVE_AMOUNT_NOT_VALID = "cn.positive.amount.not_valid";
    public static final String CN_EMAIL_NOT_VALID = "cn.email.not_valid";
    public static final String CN_BLANK = "cn.blank";
    public static final String CN_EMAIL_BLANK = "cn.email.blank";
    public static final String CN_CONFIRMATION_CODE_NOT_VALID = "cn.confirmation_code.not_valid";
    public static final String CN_USER_ID_NOT_VALID = "cn.user_id.not_valid";
    public static final String CN_PASSWORD_NOT_VALID = "cn.password.not_valid";
    public static final String CN_DATETIME_NOT_VALID = "cn.datetime.not_valid";
    public static final String CN_DUPLICATE_NAME = "cn.duplicate.name";
    public static final String CN_DUPLICATE_GROUP_NAME = "cn.duplicate.group.name";
    public static final String CN_TOO_LONG = "cn.length.too_long";
    public static final String CN_UPC_NOT_VALID = "cn.upc.not_valid";

    private static final Map<String, String> CODE_TO_MESSAGE = new ImmutableMap.Builder<String, String>()
            .put(CN_DUPLICATE_NAME, "Name already exists")
            .put(CN_DUPLICATE_GROUP_NAME, "Group with the same name already exists")
            .put(CN_INVALID_REFERENCES, "All Referenced items are invalid")
            .put(CN_INVALID_REFERENCE, "Referenced item is empty or does not exist")
            .put(CN_DATETIME_NOT_VALID, "Date and time is not valid")
            .put(CN_AMOUNT_NOT_VALID, "Amount is not a valid number")
            .put(CN_POSITIVE_AMOUNT_NOT_VALID, "Amount is not a valid positive number")
            .put(CN_EMAIL_NOT_VALID, "Email address is not valid")
            .put(CN_BLANK, "Is empty or not provided")
            .put(CN_EMAIL_BLANK, "Email address is empty or not provided")
            .put(CN_USER_ID_NOT_VALID, "User Id is not valid (only numbers and letters)")
            .put(CN_PASSWORD_NOT_VALID, "Password is not valid (1 upper case, 1 digits, LENGTH 8)")
            .put(CN_CONFIRMATION_CODE_NOT_VALID, "Confirmation Code is not valid")
            .put(CN_TOO_LONG, "Is too long")
            .put(CN_UPC_NOT_VALID, "Upc is not valid")
            .build();

    public static String reason(final String code) {
        return CODE_TO_MESSAGE.getOrDefault(code, "Unknown Reason");
    }

}
