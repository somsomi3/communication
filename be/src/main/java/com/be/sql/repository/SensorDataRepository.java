package com.be.sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.be.sql.entity.SensorDataEntity;

public interface SensorDataRepository extends JpaRepository<SensorDataEntity, Long> {
}
