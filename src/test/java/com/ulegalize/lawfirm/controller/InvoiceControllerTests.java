package com.ulegalize.lawfirm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulegalize.enumeration.DriveType;
import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.enumeration.EnumRefCurrency;
import com.ulegalize.lawfirm.EntityTest;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class InvoiceControllerTests extends EntityTest {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DataSource dataSource;
    private UsernamePasswordAuthenticationToken authentication;

    @Autowired
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setupAuthenticate() {
        LawfirmToken lawfirmToken = new LawfirmToken(1L, USER, "mail@me.com", "NEWCAB", null, true, new ArrayList<>(), "", false, EnumLanguage.FR.getShortCode(), EnumRefCurrency.EUR.getSymbol(), "", DriveType.openstack, "");

        authentication = new UsernamePasswordAuthenticationToken(lawfirmToken, null, lawfirmToken.getAuthorities());

    }

    @WithMockUser(value = "spring")
    @Test
    public void test_A_report1() throws Exception {
        mvc.perform(get("/invoice/2463")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
