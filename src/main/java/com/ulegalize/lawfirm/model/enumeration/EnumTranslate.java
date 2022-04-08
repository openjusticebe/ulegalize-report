package com.ulegalize.lawfirm.model.enumeration;

public enum EnumTranslate {
    M_001("avec", "with", "met", "mit"),
    M_002("Compte tiers", "Third parties", "Derden", "Dritte"),
    M_003("Totaux", "Totals", "Totalen", "Summen"),
    M_004("Taxe incl.", "Tax incl.", "Incl. btw.", "Steuer inkl."),
    M_005("Hors taxe", "Tax excl.", "Excl btw", "MwSt. exkl."),
    M_006("Taxe", "Tax", "Btw", "Steuer"),
    M_007("Cout total: ", "Total cost: ", "Totale prijs: ", "Gesamtkosten: "),
    M_008("Temps total: ", "Total time:", "Totale tijd:", "Gesamtzeit: "),
    M_009("Liste de Comptabilités", "Time Accounting List", "Tijdregistratielijst", "Liste der Zeitkonten"),
    M_010("Liste de Dossiers", "Folder List", "Mappenlijst", "Ordnerliste"),
    M_011("Nombre: ", "Number: ", "Nummer: ", "Anzahl: "),
    M_012("Total honoraire hors taxe", "Total fee excluding tax", "Totale vergoeding exclusief belasting", "Gesamtgebühr ohne Steuern"),
    M_013("Total factures émises hors taxe", "Total invoices issued excluding tax", "Totaal aantal uitgegeven facturen exclusief btw", "Gesamtzahl der ausgestellten Rechnungen ohne Steuern"),
    M_014("Frais de collaboration", "Collaboration fees", "Samenwerkingskosten", "Gebühren für die Zusammenarbeit"),
    M_015("Balance dossier (à facturer hors taxe)", "Balance file (to be invoiced excluding tax)", "Saldobestand (te factureren exclusief btw)", "Saldendatei (ohne MwSt. zu fakturieren)"),
    M_016("Débours", "disbursements", "uitbetalingen", "Auszahlungen"),
    M_017("Prestation", "Timesheet", "Voordeel", "Nutzen"),
    M_018("Frais administratifs", "Administrative costs", "Administratieve kosten", "Administrative Kosten"),
    M_019("Sauf erreur ou omission", "Except mistake or omission", "Behalve fout of weglating", "Außer Fehler oder Auslassungen"),
    M_020("Détails", "Details", "Details", "Einzelheiten"),
    M_021("Total Taxe incl.", "Total Tax incl.", "Totale belasting incl.", "Gesamtsteuer inkl."),
    M_022("Honoraires", "Fees", "kosten", "Gebühren"),
    M_023("Nos références : ", "Our references : ", "Onze referenties: ", "Unsere Referenzen : "),
    M_024("Concerne : ", "Concerning :", "Betreft :", "Über : "),
    M_025("État de frais et honoraires", "Statement of costs and fees", "Opgave van kosten en erelonen", "Kosten- und Gebührenaufstellung"),
    M_026("Total : ", "Total:", "Totaal:", "Gesamt:"),
    M_027("Période : ", "Period :", "Punt uit :", "Zeitraum :"),
    M_028("Montant taxe incl.", "Tax amount incl.", "BTW bedrag incl.", "Steuerbetrag inkl."),
    M_029("Montant hors taxe", "Tax amount excl.", "BTW bedrag excl.", "Steuerbetrag exkl."),
    M_030("Le montant total à payer", "The total amount to be paid", "Het totaal te betalen bedrag", "Der zu zahlende Gesamtbetrag"),
    M_031("Résumé", "Abstract", "Overzicht", "Abstrakt"),
    M_032("À VALIDER", "TO VALIDATE", "VALIDEREN", "BESTÄTIGEN"),
    M_033("Facture acquittée le ", "Invoice paid on ", "Factuur betaald op ", "Rechnung bezahlt am "),
    M_034("Dossier", "Case", "Dossier", "Fall"),
    M_035("Échéance", "Due date", "Opleveringsdatum", "Geburtstermin"),
    M_036("Annexes", "Appendices", "Bijlagen", "Anhänge"),
    M_037("Date", "Date", "Datum", "Datiert"),
    M_038("Description", "Description", "Beschrijving", "Beschreibung"),
    M_039("Taux", "Rate", "tarief", "Bewertung"),
    M_040("Page", "Page", "Bladzijde", "Buchseite"),
    M_041("Détails", "Details", "Details", "Einzelheiten"),
    M_042("Groupe par dossier", "Group by dossier", "Groeperen op dossier", "Nach Dossier gruppieren"),
    M_043("Compte", "Account", "Rekening", "Konto"),
    M_044("Balance", "Balance", "Saldobestand", "Saldendatei"),
    M_045("Tiers", "Third", "Derde", "Dritte"),
    M_046("Notes", "Notes", "Waarderingen", "Bewertungen"),
    M_047("Poste", "Job", "Functie", "Arbeit"),
    M_048("Type", "Type", "Type", "Art"),
    M_049("Balance compte tiers", "Third-party account balance", "Rekeningsaldo van derden", "Kontostand des Drittanbieters"),
    M_050("Provisions versées", "Provisions paid", "Voorzieningen betaald", "Provisionen gezahlt"),
    CHANNEL("Channel", "Channel", "Kanaal", "Kanal");

    private String labelFr;
    private String labelEn;
    private String labelNl;
    private String labelDe;

    EnumTranslate(String labelFr, String labelEn, String labelNl, String labelDe) {
        this.labelFr = labelFr;
        this.labelEn = labelEn;
        this.labelNl = labelNl;
        this.labelDe = labelDe;
    }


    public String getLabelFr() {
        return labelFr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getLabelNl() {
        return labelNl;
    }

    public String getLabelDe() {
        return labelDe;
    }
}
