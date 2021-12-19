package com.ulegalize.lawfirm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.util.AssertionErrors.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class LawfirmControllerTest {


    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetCalendarEvents() {

        try {
            mvc.perform(get("/lawfirmReport/invoice")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Should not have thrown an exception");
        }
    }


}
