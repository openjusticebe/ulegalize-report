package com.ulegalize.lawfirm.repository;

import com.ulegalize.dto.LawfirmDTO;
import com.ulegalize.lawfirm.model.entity.LawfirmEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LawfirmRepository extends CrudRepository<LawfirmEntity, String> {

    @Query(value = "SELECT new com.ulegalize.dto.LawfirmDTO(   " + "l.vckey," + "l.name," + "l.alias,"
            + "l.abbreviation," + "l.companyNumber," + "l.objetsocial," + "l.currency," + "l.website,"
            + "l.couthoraire," + "l.startInvoiceNumber," + "l.street," + "l.city," + "l.postalCode,"
            + "l.countryCode," + "l.email," + "l.phoneNumber," + "l.fax," + "l.notification," + "l.logo," + "l.driveType)"
            + " from LawfirmEntity l where upper(l.vckey) = ?1")
    Optional<LawfirmDTO> findLawfirmDTOByVckey(String vcKey);

}