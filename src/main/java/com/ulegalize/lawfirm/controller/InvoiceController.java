package com.ulegalize.lawfirm.controller;

import com.ulegalize.lawfirm.service.LawfirmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
@Slf4j
public class InvoiceController {
    @Autowired
    private LawfirmService lawfirmService;


    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> reportInvoice(@PathVariable Integer id) {
        log.debug("Entering report invoice {}", id);
        ByteArrayResource fileResponse = lawfirmService.getInvoiceReport(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice.pdf");
        log.info("Invoice generated id {}", id);


        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body((fileResponse))
                ;

    }

}
