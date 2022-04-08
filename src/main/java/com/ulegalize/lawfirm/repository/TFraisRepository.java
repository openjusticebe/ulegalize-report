package com.ulegalize.lawfirm.repository;

import com.ulegalize.lawfirm.model.entity.TFrais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface TFraisRepository extends JpaRepository<TFrais, Long>, JpaSpecificationExecutor<TFrais> {
    @Query(nativeQuery = true, value = "select *" +
            " from t_frais d" +
            " join ref_poste poste on poste.id_poste = d.id_poste" +
            " left join ref_compte compt on compt.id_compte = d.id_compte" +
            " where " +
            "   (d.vc_key = ?1)" +
            "   and (compt.account_type_id = 2)" +
            "   and compt.id_compte = coalesce (?6, compt.id_compte)" +
            "   and (case " +
            "               when ?4 = 1 then d.montantht > ?5 " +
            "               when ?4 = -1 then d.montantht < ?5 " +
            "               when ?4 = 0 then d.montantht = ?5 " +
            "               else 1=1 end" +
            "           )" +
            "   and (d.date_value between ?2 and ?3)" +
            " order by d.id_doss desc")
    List<TFrais> findByVcKeyAndTiers(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Integer balanceZero, BigDecimal balance, Long compteId);

    @Query(nativeQuery = true, value = "select COALESCE(round( sum(if(d.id_type = 2, round(COALESCE(d.montant, 0), 2), round(COALESCE(-d.montant, 0), 2) )) , 2), 0) balance " +
            " from t_frais d" +
            " join ref_compte compte on compte.id_compte = d.id_compte" +
            " left join (" +
            " select COALESCE(d.id_doss, -1) id_doss, COALESCE(SUM(if(d.id_type = 2, COALESCE(d.montantht, 0), COALESCE(-d.montantht, 0))), 0) balance" +
            " from t_frais d" +
            " join ref_compte compte on compte.id_compte = d.id_compte" +
            " where d.vc_key = ?1" +
            " and d.id_doss is not null" +
            " and compte.account_type_id = 2" +
            " and compte.id_compte = coalesce (?4, compte.id_compte)" +
            " and d.date_value between ?2 and ?3 " +
            " group by d.id_doss" +
            " ) balance on balance.id_doss = d.id_doss" +
            " where d.vc_key = ?1" +
            " and (case when ?2 = 1 then d.date_value between ?2 and ?3 else 1=1 end)" +
            " and compte.account_type_id = 2" +
            " and balance.balance <> 0" +
            " and compte.id_compte = coalesce(?4, compte.id_compte)")
    BigDecimal sumByVcKeyAndTiers(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Long compteId);

    @Query(value = "SELECT COALESCE(sum( CASE WHEN (d.idType > 1) THEN d.montant ELSE (-d.montant) END), 0)" +
            " from TFrais d " +
            "join d.refPoste poste " +
            "join d.refCompte compte " +
            "where d.idDoss = ?1 " +
            " and d.vcKey = ?2 " +
            "and compte.accountTypeId = 2")
    BigDecimal sumByVcKeyAndTiersByDossier(Long dossierId, String vcKey);

    @Query(nativeQuery = true, value = "SELECT" +
            " if(ISNULL(dossier.id_doss) = 1, 'N/A'," +
            " concat(coalesce(dossier.year_doss, 'N/A'), '/', lpad(dossier.num_doss, 4, '0'))) dossier_label," +
            " COALESCE(balance.balance, 0)," +
            " compte.compte_num," +
            " count(d.id_frais) count_tiers" +
            " from t_frais d" +
            " left join (" +
            " select COALESCE(d.id_doss, -1) id_doss, " +
            "  d.id_compte, " +
            "COALESCE(SUM(if(d.id_type = 2, COALESCE(d.montant, 0), COALESCE(-d.montant, 0))), 0) balance" +
            " from t_frais d" +
            " join ref_compte compte on compte.id_compte = d.id_compte" +
            " where d.vc_key = ?1" +
            " and d.id_doss is not null" +
            " and compte.account_type_id = 2" +
            " and compte.id_compte = coalesce (?6, compte.id_compte)" +
            " and (d.date_value between ?2 and ?3)" +
            " group by d.id_doss, d.id_compte" +
            " ) balance on balance.id_doss = d.id_doss" +
            " join ref_compte compte on compte.id_compte = balance.id_compte" +
            " left join t_dossiers dossier on dossier.id_doss = d.id_doss" +
            " where d.vc_key = ?1" +
            " and compte.account_type_id = 2" +
            " and compte.id_compte = coalesce (?6, compte.id_compte)" +
            " and (case" +
            " when ?4 = 1 then balance.balance > ?5" +
            " when ?4 = -1 then balance.balance < ?5" +
            " when ?4 = 0 then balance.balance = ?5" +
            " else 1 = 1 end)" +
            " group by d.id_doss, compte.compte_num" +
            " union all" +
            " SELECT 'N/A' dossier_label," +
            " COALESCE( balancenull.balance , 0)," +
            " COALESCE(balancenull.compte_num, 'N/A')," +
            " balancenull.count_tiers" +
            " from (" +
            " select count(d.id_frais) count_tiers, " +
            " compte.compte_num," +
            " COALESCE(SUM(if(d.id_type = 2, COALESCE(d.montant, 0), COALESCE(-d.montant, 0))), 0) balance" +
            " from t_frais d" +
            " join ref_compte compte on compte.id_compte = d.id_compte" +
            " where d.vc_key = ?1" +
            " and (d.date_value between ?2 and ?3)" +
            " and compte.account_type_id = 2" +
            " and compte.id_compte = coalesce (?6, compte.id_compte)" +
            " and d.id_doss is null" +
            " ) balancenull" +
            " where (case" +
            " when ?4 = 1 then balancenull.balance > ?5" +
            " when ?4 = -1 then balancenull.balance < ?5" +
            " when ?4 = 0 then balancenull.balance = ?5" +
            " else 1 = 1 end)" +
            " order by dossier_label desc")
    List<Object[]> findByVcKeyAndTiersGroupByDossier(String vcKey, ZonedDateTime startDate, ZonedDateTime endDate, Integer balanceZero, BigDecimal balance, Long compteId);
}