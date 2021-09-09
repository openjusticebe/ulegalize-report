package com.ulegalize.lawfirm.rest;

import com.ulegalize.lawfirm.model.LawfirmToken;
import org.springframework.web.server.ResponseStatusException;

public interface LawfirmV2Api {
    LawfirmToken getUserProfile(String email, String token) throws ResponseStatusException;
}
