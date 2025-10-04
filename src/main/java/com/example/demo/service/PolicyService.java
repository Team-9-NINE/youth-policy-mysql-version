package com.example.demo.service;

import com.example.demo.domain.Policy;
import com.example.demo.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    // application.yml에서 여러 개의 API 키를 가져올 수 있게 리스트로 지정
    @Value("#{'${youth.api-keys}'.split(',')}")
    private List<String> apiKeys;

    public List<Policy> fetchAndSavePolicies() {
        List<Policy> allPolicies = new ArrayList<>();

        try {
            for (String key : apiKeys) {
                System.out.println("현재 호출 중인 API 키: " + key);

                // 1. API 호출 URL 구성
                String url = "https://www.youthcenter.go.kr/opi/youthPolicyList.do"
                        + "?openApiVlak=" + key
                        + "&pageIndex=1"
                        + "&display=5"  // 테스트용: 5개 정책만 가져오기
                        + "&queryType=xml";

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                String xmlData = response.getBody();
                if (xmlData == null || xmlData.isEmpty()) {
                    System.out.println("API 응답이 비어 있음 - 키: " + key);
                    continue;
                }

                // 2. XML 파싱
                List<Policy> parsed = parseXml(xmlData);

                // 3. DB 저장
                policyRepository.saveAll(parsed);
                allPolicies.addAll(parsed);

                System.out.println("✅ " + key + " 키로 정책 " + parsed.size() + "개 저장 완료");

                // 4. 서버 과부하 방지를 위한 잠깐 대기 (0.5초)
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allPolicies;
    }

    private List<Policy> parseXml(String xmlData) throws Exception {
        List<Policy> result = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList list = doc.getElementsByTagName("youthPolicy");
        for (int i = 0; i < list.getLength(); i++) {
            Element el = (Element) list.item(i);

            String policyId = getTagValue("bizId", el);
            String policyName = getTagValue("polyBizSjnm", el);
            String department = getTagValue("rqutOrgNm", el);
            String region = getTagValue("polyBizSecd", el);

            Policy p = Policy.builder()
                    .policyId(policyId)
                    .policyName(policyName)
                    .department(department)
                    .region(region)
                    .build();

            result.add(p);
        }

        return result;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nlList = element.getElementsByTagName(tag);
        if (nlList.getLength() == 0) return null;

        Node nValue = nlList.item(0).getFirstChild();
        return nValue == null ? null : nValue.getNodeValue();
    }
}
