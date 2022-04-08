package com.ulegalize.lawfirm.model.converter;

import com.ulegalize.dto.ItemDto;
import com.ulegalize.dto.ItemStringDto;
import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.lawfirm.model.dto.InvoiceDTO;
import com.ulegalize.lawfirm.model.entity.TFactures;
import com.ulegalize.lawfirm.repository.TFraisRepository;
import com.ulegalize.lawfirm.utils.SuperTriConverter;
import com.ulegalize.utils.ClientsUtils;
import com.ulegalize.utils.DossiersUtils;
import com.ulegalize.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class EntityToInvoiceConverter implements SuperTriConverter<TFactures, EnumLanguage, InvoiceDTO> {
    @Autowired
    TFraisRepository tFraisRepository;

    @Override
    public InvoiceDTO apply(TFactures factures, EnumLanguage enumLanguage) {

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(factures.getIdFacture());
        invoiceDTO.setDossierId(factures.getIdDoss());
        if (factures.getTDossiers() != null) {
            invoiceDTO.setDossierLabel(DossiersUtils.getDossierLabelItem(
                    factures.getTDossiers().getYear_doss(),
                    factures.getTDossiers().getNum_doss()
            ));
            invoiceDTO.setDossierYear(factures.getTDossiers().getYear_doss());
            invoiceDTO.setDossierNumber(DossiersUtils.getDossierNum(factures.getTDossiers().getNum_doss()));
        }
        invoiceDTO.setPosteId(factures.getIdPoste());
        if (factures.getPoste() != null) {
            invoiceDTO.setPosteItem(new ItemDto(
                    factures.getIdPoste(),
                    factures.getPoste().getRefPoste()));
        }
        invoiceDTO.setClientId(factures.getIdTiers());
        if (factures.getTClients() != null) {
            Set<String> countryCodes = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3);
            List<ItemStringDto> countries = new ArrayList<>();

            for (String countryCode : countryCodes) {
                if (countryCode.equalsIgnoreCase(factures.getTClients().getId_country_alpha3())) {
                    Locale obj = new Locale(enumLanguage.getShortCode(), countryCode);
                    invoiceDTO.setCountryLabel(obj.getDisplayCountry());
                    break;
                }
            }

            invoiceDTO.setStreet(factures.getTClients().getF_rue());
            invoiceDTO.setCity(factures.getTClients().getF_cp() + " " + factures.getTClients().getF_ville());
            invoiceDTO.setVat(factures.getTClients().getF_tva());

            String fullname = ClientsUtils.getFullname(factures.getTClients().getF_nom(), factures.getTClients().getF_prenom(), factures.getTClients().getF_company());

            invoiceDTO.setClientFullName(fullname);
        }

        invoiceDTO.setVcKey(factures.getVcKey());
        invoiceDTO.setNumFacture(factures.getNumFacture());
        invoiceDTO.setYearFacture(factures.getYearFacture());
        invoiceDTO.setReference(factures.getFactureRef());

        if (factures.getIdFactureType() != null) {
            invoiceDTO.setTypeId(factures.getIdFactureType().getId());

            invoiceDTO.setTypeLabel(Utils.getLabel(enumLanguage,
                    factures.getIdFactureType().getDescriptionFr(),
                    factures.getIdFactureType().getDescriptionEn(),
                    factures.getIdFactureType().getDescriptionNl(),
                    factures.getIdFactureType().getDescriptionDe())
            );
        }
        invoiceDTO.setMontant(factures.getMontant());
        invoiceDTO.setValid(factures.getValid());
        invoiceDTO.setDateValue(factures.getDateValue());
        invoiceDTO.setDateEcheance(factures.getDateEcheance());
        if (factures.getTFactureEcheance() != null) {
            invoiceDTO.setEcheanceId(factures.getIdEcheance());
            invoiceDTO.setEcheanceItem(new ItemDto(
                    factures.getTFactureEcheance().getID(),
                    factures.getTFactureEcheance().getDESCRIPTION()));
        }

        return invoiceDTO;
    }

}
