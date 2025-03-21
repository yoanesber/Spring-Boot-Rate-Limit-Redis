package com.yoanesber.rate_limit_with_redis.service;

import java.util.List;

import com.yoanesber.rate_limit_with_redis.entity.Department;

public interface DepartmentService {
    // Save a department to the database
    Department save(Department department);

    // Find a department by id
    Department findById(String id);

    // Find all departments
    List<Department> findAll();

    // Update a department
    Department update(String id, Department department);

    // Delete a department
    void delete(String id);
}
