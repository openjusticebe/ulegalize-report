package com.ulegalize.lawfirm.model.dto;

import com.ulegalize.dto.ItemDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class InvoiceDTO {

    private Long id;
    private String idString;
    private String dossierYear;
    private String dossierNumber;
    private Long dossierId;
    private String dossierLabel;
    private String vcKey;
    private Integer numFacture;
    private Integer yearFacture;
    private String reference;
    private Long typeId;
    private String typeLabel;
    // ttc
    private BigDecimal montant;
    // htva
    private BigDecimal montantHt;
    private BigDecimal totalHonoraire;
    private Boolean valid;
    private ZonedDateTime dateValue;
    private ZonedDateTime dateEcheance;
    private Integer posteId;
    private ItemDto posteItem;
    private Integer echeanceId;
    private ItemDto echeanceItem;
    private Long clientId;
    private String clientFullName;
    private String countryLabel;
    private String street;
    private String city;
    private String vat;

}
