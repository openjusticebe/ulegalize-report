package com.ulegalize.lawfirm.repository;

import com.ulegalize.lawfirm.model.entity.TDebour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TDebourRepository extends JpaRepository<TDebour, Long>, JpaSpecificationExecutor<TDebour> {

    @Query(nativeQuery = true, value = "select" +
            " debourType.description as debourTypeDescription, debour.price_per_unit as pricePerUnit," +
            " debour.unit as unit, mesureType.description as mesureDescription" +
            " from t_factures facture" +
            "         inner join t_facture_frais_admin facture_frais_admin" +
            "                         on facture.id_facture = facture_frais_admin.facture_id" +
            " inner join t_debour debour on debour.id_debour = facture_frais_admin.DEBOURS_ID" +
            "         inner join t_debour_type debourType on debourType.id_debour_type = debour.id_debour_type" +
            "         inner join t_mesure_type mesureType on mesureType.id_mesure_type = debour.id_mesure_type" +
            " where facture.id_facture = ?1")
    List<Object[]> findAllByInvoiceIdDossierId(Long invoiceId);
}