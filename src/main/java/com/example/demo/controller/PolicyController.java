package com.example.demo.controller;

import com.example.demo.domain.Policy;
import com.example.demo.repository.PolicyRepository;
import com.example.demo.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PolicyController {

    private final PolicyService policyService;
    private final PolicyRepository policyRepository;

    // ✅ 온통청년 API에서 데이터 불러와서 DB 저장
    @GetMapping("/fetch")
    public List<Policy> fetchAndSave() {
        return policyService.fetchAndSavePolicies();
    }

    // ✅ DB에 저장된 정책들 조회
    @GetMapping("/policies")
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
}
