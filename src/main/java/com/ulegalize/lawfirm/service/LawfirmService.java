package com.ulegalize.lawfirm.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.server.ResponseStatusException;

public interface LawfirmService {
    public ByteArrayResource getInvoiceReport(Integer id) throws ResponseStatusException;

}
