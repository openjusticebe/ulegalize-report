package com.ulegalize.lawfirm.service;

import com.ulegalize.dto.LawfirmDTO;

public interface LawfirmService {
    LawfirmDTO getLawfirmByVcKey(String vcKey);
}
