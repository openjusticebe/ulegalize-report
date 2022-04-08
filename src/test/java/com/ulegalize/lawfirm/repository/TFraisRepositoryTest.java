package com.ulegalize.lawfirm.repository;

import com.ulegalize.enumeration.EnumAccountType;
import com.ulegalize.enumeration.EnumVCOwner;
import com.ulegalize.lawfirm.EntityTest;
import com.ulegalize.lawfirm.model.entity.LawfirmEntity;
import com.ulegalize.lawfirm.model.entity.RefCompte;
import com.ulegalize.lawfirm.model.entity.TDossiers;
import com.ulegalize.lawfirm.model.entity.TFrais;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class TFraisRepositoryTest extends EntityTest {
    @Autowired
    private TFraisRepository tFraisRepository;

    @Test
    void test_A_findByVcKeyAndTiers() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);

        List<TFrais> fraisList = tFraisRepository.findByVcKeyAndTiers(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), 1, tFrais.getMontantht().min(BigDecimal.ONE), null);

        assertNotNull(fraisList);
        assertTrue(fraisList.size() > 0);
    }

    @Test
    void test_A_findByVcKeyAndTiers_all() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);

        tFrais.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);

        testEntityManager.persist(tFrais.getRefCompte());

        List<TFrais> fraisList = tFraisRepository.findByVcKeyAndTiers(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), null, null, null);

        assertNotNull(fraisList);
        assertTrue(fraisList.size() > 0);
    }

    @Test
    void test_B_sumByVcKeyAndTiers() {
        LawfirmEntity lawfirm = createLawfirm("MYLAW");
        TDossiers dossier = createDossier(lawfirm, EnumVCOwner.OWNER_VC);
        RefCompte refCompte = createRefCompte(lawfirm, EnumAccountType.ACCOUNT_TIERS);

        TFrais tFrais = createTFrais(lawfirm, dossier, refCompte);
        tFrais.getRefCompte().setAccountTypeId(EnumAccountType.ACCOUNT_TIERS);

        testEntityManager.persist(tFrais.getRefCompte());

        BigDecimal countAllJusticeByVcKey = tFraisRepository.sumByVcKeyAndTiers(lawfirm.getVckey(), ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), null);

        assertNotNull(countAllJusticeByVcKey);
        assertThat(tFrais.getMontantht(), Matchers.comparesEqualTo(countAllJusticeByVcKey));
    }

}