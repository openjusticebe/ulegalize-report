package com.ulegalize.lawfirm.service;

import com.ulegalize.lawfirm.model.dto.ComptaDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface ComptaService {
    public List<ComptaDTO> getBalanceCompteTiersGroupByDossier(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Integer balanceZero, Long compteId, BigDecimal balance);

    public List<ComptaDTO> getBalanceCompteTiersWithDate(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Integer balanceZero, Long compteId, BigDecimal balance);

    public BigDecimal sumBalanceCompteTiersByDossier(String vcKey, Long dossierId);

    public BigDecimal sumBalanceCompteTiers(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Long compteId);

}
