package com.ulegalize.lawfirm.service.impl;

import com.ulegalize.lawfirm.model.converter.EntityToComptaDTOConverter;
import com.ulegalize.lawfirm.model.dto.ComptaDTO;
import com.ulegalize.lawfirm.repository.TFraisRepository;
import com.ulegalize.lawfirm.service.ComptaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ComptaServiceImpl implements ComptaService {
    private final TFraisRepository tFraisRepository;
    private final EntityToComptaDTOConverter entityToComptaDTOConverter;

    public ComptaServiceImpl(TFraisRepository tFraisRepository, EntityToComptaDTOConverter entityToComptaDTOConverter) {
        this.tFraisRepository = tFraisRepository;
        this.entityToComptaDTOConverter = entityToComptaDTOConverter;
    }

    @Override
    public List<ComptaDTO> getBalanceCompteTiersGroupByDossier(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Integer balanceZero, Long compteId, BigDecimal balance) {

        List<Object[]> resultList = tFraisRepository.findByVcKeyAndTiersGroupByDossier(vcKey, startDate, endDate, balanceZero, balance, compteId);
        return resultList.stream().map(r -> {
            ComptaDTO comptaDTO = new ComptaDTO();
            comptaDTO.setDossierLabel(r[0] != null ? (String) r[0] : null);
            comptaDTO.setBalance(r[1] != null ? (BigDecimal) r[1] : null);
            comptaDTO.setIdTypeItem(r[2] != null ? (String) r[2] : null);
            return comptaDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ComptaDTO> getBalanceCompteTiersWithDate(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Integer balanceZero, Long compteId, BigDecimal balance) {
        return entityToComptaDTOConverter.convertToList(tFraisRepository.findByVcKeyAndTiers(vcKey, startDate, endDate, balanceZero, balance, compteId));
    }

    @Override
    public BigDecimal sumBalanceCompteTiersByDossier(String vcKey, Long dossierId) {
        return tFraisRepository.sumByVcKeyAndTiersByDossier(dossierId, vcKey);
    }

    @Override
    public BigDecimal sumBalanceCompteTiers(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Long compteId) {
        return tFraisRepository.sumByVcKeyAndTiers(vcKey, startDate, endDate, compteId);
    }
}
