package com.ulegalize.lawfirm.model.converter;

import com.ulegalize.dto.ItemLongDto;
import com.ulegalize.lawfirm.model.dto.ComptaDTO;
import com.ulegalize.lawfirm.model.entity.TFrais;
import com.ulegalize.lawfirm.utils.SuperConverter;
import com.ulegalize.utils.ClientsUtils;
import com.ulegalize.utils.DossiersUtils;
import org.springframework.stereotype.Component;

@Component
public class EntityToComptaDTOConverter implements SuperConverter<TFrais, ComptaDTO> {

    @Override
    public ComptaDTO apply(TFrais entityTFrais) {
        ComptaDTO comptaDTO = new ComptaDTO();
        comptaDTO.setId(entityTFrais.getIdFrais());
        comptaDTO.setVcKey(entityTFrais.getVcKey());
        comptaDTO.setGridId(entityTFrais.getGridId());
        comptaDTO.setIdCompte(entityTFrais.getIdCompte());
        if (entityTFrais.getRefCompte() != null) {
            comptaDTO.setCompteLabel(entityTFrais.getRefCompte().getCompteNum());
        }
        comptaDTO.setIdDoss(entityTFrais.getIdDoss());
        if (entityTFrais.getTDossiers() != null) {
            comptaDTO.setDossierLabel(
                    DossiersUtils.getDossierLabelItem(entityTFrais.getTDossiers().getYear_doss(),
                            entityTFrais.getTDossiers().getNum_doss()));
        }
        comptaDTO.setIdFacture(entityTFrais.getIdFacture());
        comptaDTO.setIdUser(entityTFrais.getIdClient());
        if (entityTFrais.getIdClient() != null && entityTFrais.getTClients() != null) {
            String fullname = ClientsUtils.getFullname(entityTFrais.getTClients().getF_nom(), entityTFrais.getTClients().getF_prenom(), entityTFrais.getTClients().getF_company());
            comptaDTO.setIdUserItem(new ItemLongDto(entityTFrais.getIdClient(), fullname));
            comptaDTO.setTiersFullname(fullname);
        }
        comptaDTO.setMontant(entityTFrais.getMontant());
        comptaDTO.setMemo(entityTFrais.getMemo());
        comptaDTO.setIdPost(entityTFrais.getIdPoste());
        comptaDTO.setIdTransaction(entityTFrais.getIdTransaction().getId());
        comptaDTO.setIdType(entityTFrais.getIdType().getIdType());
        comptaDTO.setIdTypeItem(entityTFrais.getIdType().getDescriptionFr());
        comptaDTO.setMontantHt(entityTFrais.getMontantht());
        comptaDTO.setRatio(entityTFrais.getRatio());
        comptaDTO.setDateValue(entityTFrais.getDateValue().toLocalDate());
        if (entityTFrais.getRefPoste() != null) {
            comptaDTO.setPosteLabel(entityTFrais.getRefPoste().getRefPoste());
        }
        return comptaDTO;
    }
}
