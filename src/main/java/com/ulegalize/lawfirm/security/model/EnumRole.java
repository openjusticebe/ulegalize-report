package com.ulegalize.lawfirm.security.model;

public enum EnumRole {
    ADMINISTRATEUR(0, "Administrateur"),
    TIERS_LECTURE(1, "Tiers - lecture"),
    DOSSIERS_LECTURE(2, "Dossiers - lecture"),
    COMPTABILITE_LECTURE(3, "Comptabilité - lecture"),
    ECHEANCIER_LECTURE(4, "Echéancier - lecture"),
    COMPTABILITÉ_IMPRESSIONDERAPPORTS(6, "Comptabilité - impression de rapports"),
    DOSSIERS_VOIRASPECTSFINANCIERS(7, "Dossiers -  voir aspects financiers"),
    DOSSIERS_VOIRNOTEAVOCAT(13, "Dossiers - voir note avocat"),
    COMPTABILITE_ECRITURE(15, "Comptabilité - écriture"),
    DOSSIERS_FINANCIER(17, "Dossiers - Financier"),
    TIMESHEET_ECRITURE(18, "Timesheet - écriture"),
    DOSSIERS_DOCUMENTS(20, "Dossiers - Documents"),
    DOSSIERS_ECRITURE(21, "Dossiers - écriture"),
    TIERS_ECRITURE(22, "Tiers - écriture"),
    TABLEAUDEBORD(23, "Tableau de bord"),
    ECHEANCIER_ECRITURE(24, "Echéancier - écriture"),
    TIMESHEET_LECTURE(25, "Timesheet - lecture"),
    FACTURE_LECTURE(26, "Facture - lecture"),
    FACTURE_ECRITURE(27, "Facture - écriture"),
    FACTURE_VALIDATION(28, "Facture - validation"),
    FACTURE_CONFIGURATION(29, "Facture - configuration"),
    AVODRIVE_LECTURE(30, "Avodrive - Lecture"),
    AVODRIVE_ECRITURE(31, "Avodrive - Ecriture"),
    AVODRIVE_PARTAGE(32, "Avodrive - Partage"),
    GESTION_DU_COURIER_ENTRANT(33, "Gestion du courier entrant"),
    CALENDAR_LECTURE(34, "Calendar - lecture"),
    CALENDAR_ECRITURE(35, "Calendar - ecriture"),
    JUSTICIABLE_ECRITURE(36, "Justiciable - ecriture"),
    JUSTICIABLE_ADMIN(37, "Justiciable - admin");

    private int id;
    private String description;

    EnumRole(int id1, String description1) {
        this.id = id1;
        this.description = description1;
    }

    public static EnumRole fromId(Long id) {
        for (EnumRole role : values()) {
            if (role.getId() == id)
                return role;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}