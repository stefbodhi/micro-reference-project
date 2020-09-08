package com.templates;

import java.util.Optional;

public enum Template {

    PARENT_HTML("html/parent"),

    ACTIVATE_ACCOUNT("html/activate_account", "activate_account", PARENT_HTML),
    CONFIRM_ACCOUNT("html/confirm_account", "confirm_account", PARENT_HTML),
    MTAN_CODE("html/mtan_code", "mtan_code", PARENT_HTML),
    NEW_INVITED_USER("html/new_invited_user", "new_invited_user", PARENT_HTML),
    NEW_USER("html/new_user", "new_user", PARENT_HTML),

    QR_CODE("html/qr_code"),
    SEARCH("html/search"),
    RESET_PASSWORD("html/reset_password","reset_password",PARENT_HTML),
    OTP_LOGIN("html/otp_login","otp_login",PARENT_HTML);

    public String page;
    public String fragment = null;
    public Template master = null;

    Template(String page) {
        this.page = page;
    }

    Template(String page, String fragment, Template master) {
        this.page = page;
        this.fragment = fragment;
        this.master = master;
    }

    public Optional<String> getPage() {
        return Optional.of(page);
    }

    public Optional<String> getFragment() {
        return Optional.ofNullable(fragment);
    }

    public Optional<Template> getMaster() {
        return Optional.ofNullable(master);
    }
}
