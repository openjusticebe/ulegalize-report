package com.ulegalize.lawfirm.controller;

import com.ulegalize.enumeration.EnumLanguage;
import com.ulegalize.lawfirm.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
@Slf4j
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;


    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> reportInvoice(@PathVariable Long id, @RequestParam(required = false) Integer responsableId,
                                                  @RequestParam(required = false) String language
    ) {
        log.debug("Entering report invoice {}", id);


        EnumLanguage enumLanguage = EnumLanguage.FR;
        if (language != null) {
            enumLanguage = EnumLanguage.fromshortCode(language);
        }
        ByteArrayResource fileResponse = invoiceService.getInvoiceReport(id, enumLanguage);

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
