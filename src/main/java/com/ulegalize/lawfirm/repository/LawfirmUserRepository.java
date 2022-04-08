package com.ulegalize.lawfirm.repository;

import com.ulegalize.lawfirm.model.entity.LawfirmUsers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
@Repository
public interface LawfirmUserRepository extends CrudRepository<LawfirmUsers, Long> {

    @Query(value = "SELECT l from LawfirmUsers l where l.lawfirm.vckey = ?1 and l.user.id = ?2")
    Optional<LawfirmUsers> findLawfirmUsersByVcKeyAndUserId(String vcKey, Long userId);
}
