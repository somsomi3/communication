package com.be.opcua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.be.dto.SensorMessage;
import com.be.service.KafkaProducerService;

import java.util.List;
import java.util.Map;

//공장 설비(OPC-UA)에서 나온 값을우리 시스템이 이해하는 ‘표준 메시지’로 받아오는 입구를 만드는 작업

//OPC-UA Client (Eclipse Milo)
// → 설비 서버에 접속
// → 특정 변수(NodeId)를 읽고
// → 그 값을 우리 시스템 메시지로 변환
// → Kafka로 던짐
@Component
@RequiredArgsConstructor
public class OpcUaClientRunner {

    private OpcUaClient client;
    private final KafkaProducerService kafkaProducerService;
    //Kafka로 보낼 때
    // 스케줄링할 때
    // 여러 메서드에서 재사용하려고

     // 읽을 NodeId 목록
    private final List<NodeId> nodeIds = List.of(
            new NodeId(2, "HelloWorld/ScalarTypes/Int32"),
            new NodeId(2, "HelloWorld/ScalarTypes/Float"),
            new NodeId(2, "HelloWorld/ScalarTypes/Double")
    );

    //연결은 한번
    @PostConstruct
    public void init() {
        try {
            // 1️. OPC-UA 서버 Endpoint
            //로컬 OPC-UA Demo Server 주소 (통신 대상 위치)
            String endpointUrl = "opc.tcp://localhost:12686/milo";


            // 2️. Endpoint discovery (0.6.7 → List 반환)
            List<EndpointDescription> endpoints =
                    DiscoveryClient.getEndpoints(endpointUrl).get();

            EndpointDescription endpoint = endpoints.stream()
                    .filter(e -> e.getSecurityPolicyUri()
                            .equals(SecurityPolicy.None.getUri()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("No endpoint with SecurityPolicy.None"));

            // 3️. ClientConfig 생성 (0.6.7 필수)
            OpcUaClientConfig config = OpcUaClientConfig.builder()
                    .setEndpoint(endpoint)
                    .build();

            // 4️. Client 생성 + 연결
            client = OpcUaClient.create(config);
            client.connect().get();

            System.out.println("[OPC-UA] Connected to server");

        } catch (Exception e) {
            System.err.println("[OPC-UA] Error occurred");
            e.printStackTrace();
        }
    }


    // 5초마다 OPC-UA 값 읽기
    @Scheduled(fixedRate = 5000)    //값 계속 읽기
    public void readNodes() {
        try {
            for (NodeId nodeId : nodeIds) {
                DataValue dataValue =
                        client.readValue(0, TimestampsToReturn.Both, nodeId).get();

                Object sensorValue = dataValue.getValue().getValue();

                System.out.println(
                        "[OPC-UA] " + nodeId.getIdentifier() + " = " + sensorValue
                );

            SensorMessage message = new SensorMessage();
            message.setProtocol("opc-ua");
            message.setSourceId(nodeId.getIdentifier().toString());
            message.setType(sensorValue.getClass().getSimpleName());
            message.setTimestamp(System.currentTimeMillis());
            message.setPayload(Map.of("value", sensorValue));;

            kafkaProducerService.sendSensorMessage(
                    "opcua-data",
                    message
            );
        }
    } catch (Exception e) {
        System.err.println("[OPC-UA] Read & Kafka publish error");
        e.printStackTrace();
    }
}
}