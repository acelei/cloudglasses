package com.cloudglasses.repository;

import com.cloudglasses.model.SystemConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigRepository extends CrudRepository<SystemConfig, String> {
}
