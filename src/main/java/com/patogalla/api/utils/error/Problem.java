package com.patogalla.api.utils.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import reactor.util.annotation.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Problem {

    @NonNull
    private final String type = "about:blank";

    @NotNull
    private final String title;

    private final String detail;

    private final List<InvalidParam> invalidParams;

    public Problem(@NotNull String title, String detail, List<InvalidParam> invalidParams) {
        this.title = title;
        this.detail = detail;
        this.invalidParams = invalidParams;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public List<InvalidParam> getInvalidParams() {
        return invalidParams;
    }
}
