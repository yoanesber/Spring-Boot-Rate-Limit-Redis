package com.yoanesber.rate_limit_with_redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoanesber.rate_limit_with_redis.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    
}
