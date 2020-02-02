package com.cloudglasses.repository;

import com.cloudglasses.model.OptometryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OptometryDetailRepository extends JpaRepository<OptometryDetail, Long>, JpaSpecificationExecutor<OptometryDetail> {
    OptometryDetail findFirstByMobileAndStatusTrueOrderByCreateTimeDesc(String mobile);
}
