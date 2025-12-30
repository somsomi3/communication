package com.be.sql.entity;


//entity 패키지: JPA가 DB 테이블과 1:1로 매핑하는 객체
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sensor_data")
public class SensorDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String protocol;
    private String sourceId;
    private String type;

    //payload에서 선별한 핵심 데이터
    private Double value;
    private String unit;

    //센서가 데이터를 측정한 시각(정렬 / 집계 / 통계에 필수)
    private Instant timestamp;

    public SensorDataEntity() {}

    // getter / setter
    public Long getId() { return id; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
