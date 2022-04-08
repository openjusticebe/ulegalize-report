package com.ulegalize.lawfirm.service.impl;

import com.ulegalize.enumeration.EnumAccountType;
import com.ulegalize.enumeration.EnumTType;
import com.ulegalize.enumeration.EnumVCOwner;
import com.ulegalize.lawfirm.EntityTest;
import com.ulegalize.lawfirm.model.dto.ComptaDTO;
import com.ulegalize.lawfirm.model.entity.LawfirmEntity;
import com.ulegalize.lawfirm.model.entity.RefCompte;
import com.ulegalize.lawfirm.model.entity.TDossiers;
import com.ulegalize.lawfirm.model.entity.TFrais;
import com.ulegalize.lawfirm.service.ComptaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@Slf4j
class ComptaServiceImplTest extends EntityTest {
    @Autowired
    private ComptaService comptaService;

    @Test
    void test_A_getBalanceCompteTiers() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);

        tFrais.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);

        testEntityManager.persist(tFrais.getRefCompte());

        List<ComptaDTO> comptaDTOList = comptaService.getBalanceCompteTiersWithDate(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), null, null, null);

        assertNotNull(comptaDTOList);
        assertTrue(comptaDTOList.size() > 0);
    }

    @Test
    @Disabled
    void test_B_getBalanceCompteTiersGroupByDossier_balanceZero_is_null() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);

        tFrais.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);

        testEntityManager.persist(tFrais.getRefCompte());

        List<ComptaDTO> comptaDTOList = comptaService.getBalanceCompteTiersGroupByDossier(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), null, null, null);

        assertNotNull(comptaDTOList);
        assertTrue(comptaDTOList.size() > 0);
    }

    @Test
    @Disabled
    void test_C_getBalanceCompteTiersGroupByDossier_balanceZero_is_false() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);
        TFrais tFrais2 = createTFrais(lawfirm, dossier, refCompte);
        tFrais2.setIdType(EnumTType.SORTIE);
        testEntityManager.persist(tFrais2);

        tFrais.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);
        tFrais2.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);

        testEntityManager.persist(tFrais.getRefCompte());
        testEntityManager.persist(tFrais2.getRefCompte());

        List<ComptaDTO> comptaDTOList = comptaService.getBalanceCompteTiersGroupByDossier(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), -1, null, BigDecimal.ZERO);

        assertNotNull(comptaDTOList);
        assertTrue(comptaDTOList.size() > 0);
    }

    @Test
    @Disabled
    void test_D_getBalanceCompteTiersGroupByDossier_balanceZero_is_true() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);
        TFrais tFrais2 = createTFrais(lawfirm, dossier, refCompte);

        tFrais.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);
        tFrais2.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);

        testEntityManager.persist(tFrais.getRefCompte());
        testEntityManager.persist(tFrais2.getRefCompte());

        List<ComptaDTO> comptaDTOList = comptaService.getBalanceCompteTiersGroupByDossier(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), 1, null, BigDecimal.ONE);

        assertNotNull(comptaDTOList);
        assertTrue(comptaDTOList.size() > 0);
    }
}