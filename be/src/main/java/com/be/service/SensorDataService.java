package com.be.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.be.dto.SensorMessage;
import com.be.sql.entity.SensorDataEntity;
import com.be.sql.repository.SensorDataRepository;

//TcpServer → Repository이면:
//TCP 전용 로직에 DB 로직이 섞임
// UDP, Kafka, WebSocket에서 재사용 불가
// 테스트하기 어려움

//=> Service 분리

@Service
public class SensorDataService {

    private final SensorDataRepository repository;

    public SensorDataService(SensorDataRepository repository) {
        this.repository = repository;
    }

    public void saveFromMessage(SensorMessage sm) {

        SensorDataEntity entity = new SensorDataEntity();

        entity.setProtocol(sm.getProtocol());
        entity.setSourceId(sm.getSourceId());
        entity.setType(sm.getType());
        // 센서 측정 시각
        entity.setTimestamp(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(sm.getTimestamp()),
                ZoneId.systemDefault()
            )
        );
        // 서버 저장 시각
        entity.setCreatedAt(LocalDateTime.now());


        Map<String, Object> payload = sm.getPayload();
        if (payload != null) {
            Object value = payload.get("value");
            Object unit = payload.get("unit");

            if (value != null) {
                entity.setValue(Double.valueOf(value.toString()));
            }

            if (unit != null) {
                entity.setUnit(unit.toString());
            }
        }

        repository.save(entity);
    }
}
