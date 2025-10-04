package com.example.demo.repository;

import com.example.demo.domain.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔티티명, 기본키 자료형>
public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
