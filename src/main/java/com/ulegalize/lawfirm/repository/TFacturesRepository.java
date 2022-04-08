package com.ulegalize.lawfirm.repository;

import com.ulegalize.lawfirm.model.entity.TFactures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TFacturesRepository extends JpaRepository<TFactures, Long>, JpaSpecificationExecutor<TFactures> {
    Optional<TFactures> findByIdFactureAndVcKey(Long factureId, String vcKey);

}