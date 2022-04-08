package com.ulegalize.lawfirm.service;

import com.ulegalize.enumeration.EnumLanguage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.server.ResponseStatusException;

public interface InvoiceService {
    public ByteArrayResource getInvoiceReport(Long id, EnumLanguage enumLanguage) throws ResponseStatusException;

}