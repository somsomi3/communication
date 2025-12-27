package com.be.dto;

import java.util.Map;
//데이터 설계도
//센서데이터의 모양을 정의하는 클래스(통신 데이터의 표준 포맷)
//TCP / UDP / MQTT / WebSocket / Kafka 모두 이런형태로 흘러감.
public class SensorMessage {
    private String protocol;
    private String sourceId;
    private String type;
    private long timestamp;
    private Map<String, Object> payload;

    public SensorMessage() {}

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}
