package com.ulegalize.lawfirm.model.enumeration;

public enum EnumTranslate {
    M_100("fr"),
    M_101("fr"),
    M_102("fr");

    private String french;

    EnumTranslate(String french) {
        this.french = french;
    }

    public static EnumTranslate fromshortCode(String shortCode) {
        for (EnumTranslate language : EnumTranslate.values()) {
            if (language.getFrench().equalsIgnoreCase(shortCode)) {
                return language;
            }
        }

        return null;
    }

    public String getFrench() {
        return french;
    }
}
