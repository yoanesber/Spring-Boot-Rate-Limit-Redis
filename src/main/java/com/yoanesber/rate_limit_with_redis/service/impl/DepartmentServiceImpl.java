package com.yoanesber.rate_limit_with_redis.service.impl;

import jakarta.transaction.Transactional;
import java.util.concurrent.TimeUnit;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yoanesber.rate_limit_with_redis.entity.Department;
import com.yoanesber.rate_limit_with_redis.repository.DepartmentRepository;
import com.yoanesber.rate_limit_with_redis.service.DepartmentService;
import com.yoanesber.rate_limit_with_redis.service.RedisService;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final RedisService redisService;
    private static final String DEPARTMENT_CACHE_KEY_PREFIX = "department:";
    private static final String DEPARTMENTLIST_CACHE_KEY = "department-list";
    private static final long timeout = 0;
    private static final TimeUnit unit = null;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
        RedisService redisService) {
        this.departmentRepository = departmentRepository;
        this.redisService = redisService;
    }

    @Transactional
    @Override
    public Department save(Department department) {
        Assert.notNull(department, "Department cannot be null");

        try {
            // Check if department already exists   
            Department existingDepartment = departmentRepository.findById(department.getId()).orElse(null);

            // If department does not exist, save it    
            if (existingDepartment != null) {
                throw new RuntimeException("Department with id " + department.getId() + " already exists");
            }

            // Save department to database
            Department savedDepartment = departmentRepository.save(department);

            // Save department to Redis
            redisService.set(DEPARTMENT_CACHE_KEY_PREFIX + savedDepartment.getId(), savedDepartment, timeout, unit);

            // Replace the list with the latest data
            redisService.setList(DEPARTMENTLIST_CACHE_KEY, departmentRepository.findAll(), timeout, unit);

            return savedDepartment;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public Department findById(String id) {
        Assert.notNull(id, "Department id cannot be null");

        try {
            // Check if department exists in Redis
            Department department = (Department) redisService.get(DEPARTMENT_CACHE_KEY_PREFIX + id, Department.class);

            // If department does not exist in Redis, fetch it from the database
            if (department == null) {
                department = departmentRepository.findById(id).orElse(null);

                // If department exists, save it to Redis
                if (department != null) {
                    redisService.set(DEPARTMENT_CACHE_KEY_PREFIX + id, department, timeout, unit);

                    // Replace the list with the latest data
                    redisService.setList(DEPARTMENTLIST_CACHE_KEY, departmentRepository.findAll(), timeout, unit);
                }
            }
            
            return department;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public List<Department> findAll() {
        try {
            // Check if departments exist in Redis
            List<Department> departments = redisService.getList(DEPARTMENTLIST_CACHE_KEY, Department.class);

            // If departments do not exist in Redis, fetch them from the database
            if (departments == null || departments.isEmpty()) {
                departments = departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

                // If departments exist, save them to Redis
                if (departments != null && !departments.isEmpty()) {
                    redisService.setList(DEPARTMENTLIST_CACHE_KEY, departments, timeout, unit);
                }
            }

            return departments;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public Department update(String id, Department department) {
        Assert.notNull(id, "Department id cannot be null");
        Assert.notNull(department, "Department cannot be null");

        try {
            // Check if department exists
            Department existingDepartment = departmentRepository.findById(id).orElse(null);

            // If department does not exist, throw an exception
            if (existingDepartment == null) {
                throw new RuntimeException("Department with id " + id + " does not exist");
            }

            // Update department in database
            existingDepartment.setDeptName(department.getDeptName());
            existingDepartment.setActive(department.isActive());
            existingDepartment.setUpdatedBy(department.getUpdatedBy());
            existingDepartment.setUpdatedDate(department.getUpdatedDate());
            Department updatedDepartment = departmentRepository.save(existingDepartment);

            // Save updated department to Redis
            redisService.set(DEPARTMENT_CACHE_KEY_PREFIX + id, updatedDepartment, timeout, unit);

            // Replace the list with the latest data
            redisService.setList(DEPARTMENTLIST_CACHE_KEY, departmentRepository.findAll(), timeout, unit);

            return updatedDepartment;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void delete(String id) {
        Assert.notNull(id, "Department id cannot be null");

        try {
            // Check if department exists
            Department existingDepartment = departmentRepository.findById(id).orElse(null);

            // If department does not exist, throw an exception
            if (existingDepartment == null) {
                throw new RuntimeException("Department with id " + id + " does not exist");
            }

            // Delete department from database
            departmentRepository.deleteById(id);

            // Delete department from Redis
            redisService.delete(DEPARTMENT_CACHE_KEY_PREFIX + id);

            // Replace the list with the latest data
            redisService.setList(DEPARTMENTLIST_CACHE_KEY, departmentRepository.findAll(), timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
