package com.patogalla.api.user.model;

import com.google.common.collect.Lists;

import java.util.List;

public enum Role {
    admin{
        @Override
        public List<String> getPermissions() {
            return Lists.newArrayList("USER_UPDATE");
        }
    },
    user, analyst, thirdparty;

    public List<String> getPermissions() {
        return Lists.newArrayList();
    }
}
