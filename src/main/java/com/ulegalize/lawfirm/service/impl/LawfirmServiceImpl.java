package com.ulegalize.lawfirm.service.impl;

import com.ulegalize.dto.LawfirmDTO;
import com.ulegalize.lawfirm.repository.LawfirmRepository;
import com.ulegalize.lawfirm.service.LawfirmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class LawfirmServiceImpl implements LawfirmService {

    private final LawfirmRepository lawfirmRepository;

    public LawfirmServiceImpl(LawfirmRepository lawfirmRepository) {
        this.lawfirmRepository = lawfirmRepository;
    }

    @Override
    public LawfirmDTO getLawfirmByVcKey(String vcKey) {
        log.debug("Entering getLawfirmByVcKey {}", vcKey);

        Optional<LawfirmDTO> lawfirmDTOOptional = lawfirmRepository.findLawfirmDTOByVckey(vcKey);
        return lawfirmDTOOptional.orElse(null);
    }
}
