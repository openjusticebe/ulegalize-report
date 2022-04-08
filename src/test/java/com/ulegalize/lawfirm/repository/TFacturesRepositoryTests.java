package com.ulegalize.lawfirm.repository;

import com.ulegalize.lawfirm.EntityTest;
import com.ulegalize.lawfirm.model.entity.LawfirmEntity;
import com.ulegalize.lawfirm.model.entity.TFactures;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@TestMethodOrder(MethodOrderer.MethodName.class)
//@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class TFacturesRepositoryTests extends EntityTest {
    @Autowired
    TFacturesRepository tFacturesRepository;

    @Test
    public void test_A_findByIdFactureAndVcKey() {

        LawfirmEntity lawfirmEntity = createLawfirm("MYLAW");
        TFactures tFactures = createFacture(lawfirmEntity);

        Optional<TFactures> optionalTFactures = tFacturesRepository.findByIdFactureAndVcKey(tFactures.getIdFacture(), lawfirmEntity.getVckey());

        assertNotNull(optionalTFactures);
        assertTrue(optionalTFactures.isPresent());
    }

    @Test
    public void test_B_getInvoiceById() {
        LawfirmEntity lawfirmEntity = createLawfirm("MYLAW");
        TFactures tFactures = createFacture(lawfirmEntity);

        Optional<TFactures> factureFounded = tFacturesRepository.findById(tFactures.getIdFacture());

        assertNotNull(factureFounded);
        assertTrue(factureFounded.isPresent());
        assertEquals(tFactures.getIdEcheance(), factureFounded.get().getIdEcheance());
    }

}
