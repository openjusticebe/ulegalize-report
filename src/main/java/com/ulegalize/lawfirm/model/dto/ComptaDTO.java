package com.ulegalize.lawfirm.model.dto;

import com.ulegalize.dto.ItemLongDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ComptaDTO {
    Long id;
    Integer idType;
    String idTypeItem;
    Integer idCompte;
    String compteLabel;
    LocalDate dateValue;
    BigDecimal montant;
    BigDecimal montantHt;
    Integer idTransaction;
    BigDecimal ratio;
    Integer gridId;
    Integer idPost;
    String posteLabel;
    Long idUser;
    ItemLongDto idUserItem;
    Long idDoss;
    String dossierLabel;
    Long idFacture;
    String memo;
    String vcKey;
    String tiersFullname;
    BigDecimal balance;

    public ComptaDTO() {
    }
}
