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
import org.springframework.stereotype.Component;

import java.util.List;

//공장 설비(OPC-UA)에서 나온 값을우리 시스템이 이해하는 ‘표준 메시지’로 받아오는 입구를 만드는 작업

//OPC-UA Client (Eclipse Milo)
// → 설비 서버에 접속
// → 특정 변수(NodeId)를 읽고
// → 그 값을 우리 시스템 메시지로 변환
// → Kafka로 던짐
@Component
public class OpcUaClientRunner {


    @PostConstruct
    public void start() {
        run();
    }

    public void run() {
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
            OpcUaClient client = OpcUaClient.create(config);
            client.connect().get();

            System.out.println("[OPC-UA] Connected to server");

            // 5️. 여러 NodeId 정의
            List<NodeId> nodeIds = List.of(
                    new NodeId(2, "HelloWorld/ScalarTypes/Int32"),
                    new NodeId(2, "HelloWorld/ScalarTypes/Float"),
                    new NodeId(2, "HelloWorld/ScalarTypes/Double")
            );
            
            // 6. NodeId별 값 읽기
            for (NodeId nodeId : nodeIds) {
                DataValue value =
                        client.readValue(0, TimestampsToReturn.Both, nodeId).get();

                System.out.println(
                        "[OPC-UA] " + nodeId.getIdentifier() +
                        " = " + value.getValue().getValue()
                );
            }


        } catch (Exception e) {
            System.err.println("[OPC-UA] Error occurred");
            e.printStackTrace();
        }
    }
}
