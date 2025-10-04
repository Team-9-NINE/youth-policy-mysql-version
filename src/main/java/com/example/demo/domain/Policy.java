package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 자동 증가 기본키

    private String policyId;       // 정책 고유 ID
    private String policyName;     // 정책명
    private String department;     // 담당기관명
    private String region;         // 지역 구분
}
