package com.cloudglasses.repository;

import com.cloudglasses.model.OptometryDetail;
import com.cloudglasses.model.WeixinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WeixinUserRepository extends JpaRepository<WeixinUser, String>, JpaSpecificationExecutor<OptometryDetail> {
}
