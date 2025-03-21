package com.yoanesber.rate_limit_with_redis.controller;

import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.rate_limit_with_redis.dto.SaveDepartmentRequestDTO;
import com.yoanesber.rate_limit_with_redis.dto.UpdateDepartmentRequestDTO;
import com.yoanesber.rate_limit_with_redis.entity.CustomHttpResponse;
import com.yoanesber.rate_limit_with_redis.entity.Department;
import com.yoanesber.rate_limit_with_redis.service.DepartmentService;
import com.yoanesber.rate_limit_with_redis.service.RateLimitService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    // Rate limit configuration
    private static final String RATE_LIMIT_POSTFIX_SAVE = ".save-department";
    private static final String RATE_LIMIT_POSTFIX_FIND_ALL = ".find-all-departments";
    private static final String RATE_LIMIT_POSTFIX_FIND_BY_ID = ".find-department-by-id";
    private static final String RATE_LIMIT_POSTFIX_UPDATE = ".update-department";
    private static final String RATE_LIMIT_POSTFIX_DELETE = ".delete-department";
    private static final int MAX_REQUESTS = 5;
    private static final long DURATION = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    // Inject services and dependencies
    private final DepartmentService departmentService;
    private final RateLimitService rateLimitService;

    public DepartmentController(DepartmentService departmentService,
        RateLimitService rateLimitService) {
        this.departmentService = departmentService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ResponseEntity<CustomHttpResponse> save(@RequestBody SaveDepartmentRequestDTO departmentDTO, HttpServletRequest request) {
        Assert.notNull(departmentDTO, "DepartmentDTO cannot be null");

        try {
            // Use the client unique identifier if available, e.g., user ID
            final String RATE_LIMIT_KEY = request.getRemoteAddr() + RATE_LIMIT_POSTFIX_SAVE;

            // Check if the request is allowed
            if (!rateLimitService.isAllowed(RATE_LIMIT_KEY, MAX_REQUESTS, DURATION, TIME_UNIT)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new CustomHttpResponse(HttpStatus.TOO_MANY_REQUESTS.value(), 
                    "Too many requests", null));
            }

            // Save department & return response
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomHttpResponse(HttpStatus.CREATED.value(), 
                "Department saved successfully", departmentService.save(new Department(departmentDTO))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An error occurred while saving department", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<CustomHttpResponse> findAll(HttpServletRequest request) {
        try {
            // Use the client unique identifier if available, e.g., user ID
            final String RATE_LIMIT_KEY = request.getRemoteAddr() + RATE_LIMIT_POSTFIX_FIND_ALL;

            // Check if the request is allowed
            if (!rateLimitService.isAllowed(RATE_LIMIT_KEY, MAX_REQUESTS, DURATION, TIME_UNIT)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new CustomHttpResponse(HttpStatus.TOO_MANY_REQUESTS.value(), 
                    "Too many requests", null));
            }

            // Return all departments
            return ResponseEntity.status(HttpStatus.OK)
                .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Departments retrieved successfully", departmentService.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An error occurred while retrieving departments", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomHttpResponse> findById(@PathVariable("id") String id, HttpServletRequest request) {
        Assert.notNull(id, "Id cannot be null");

        try {
            // Use the client unique identifier if available, e.g., user ID
            final String RATE_LIMIT_KEY = request.getRemoteAddr() + RATE_LIMIT_POSTFIX_FIND_BY_ID;

            // Check if the request is allowed
            if (!rateLimitService.isAllowed(RATE_LIMIT_KEY, MAX_REQUESTS, DURATION, TIME_UNIT)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new CustomHttpResponse(HttpStatus.TOO_MANY_REQUESTS.value(), 
                    "Too many requests", null));
            }

            // Return department by id
            return ResponseEntity.status(HttpStatus.OK)
                .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Department retrieved successfully", departmentService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An error occurred while retrieving department", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomHttpResponse> update(@PathVariable("id") String id, @RequestBody UpdateDepartmentRequestDTO departmentDTO, HttpServletRequest request) {
        Assert.notNull(id, "Id cannot be null");
        Assert.notNull(departmentDTO, "DepartmentDTO cannot be null");

        try {
            // Use the client unique identifier if available, e.g., user ID
            final String RATE_LIMIT_KEY = request.getRemoteAddr() + RATE_LIMIT_POSTFIX_UPDATE;

            // Check if the request is allowed
            if (!rateLimitService.isAllowed(RATE_LIMIT_KEY, MAX_REQUESTS, DURATION, TIME_UNIT)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new CustomHttpResponse(HttpStatus.TOO_MANY_REQUESTS.value(), 
                    "Too many requests", null));
            }

            // Update department & return response
            return ResponseEntity.status(HttpStatus.OK)
                .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Department updated successfully", departmentService.update(id, new Department(departmentDTO))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An error occurred while updating department", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomHttpResponse> delete(@PathVariable("id") String id,
        HttpServletRequest request) {
        Assert.notNull(id, "Id cannot be null");

        try {
            // Use the client unique identifier if available, e.g., user ID
            final String RATE_LIMIT_KEY = request.getRemoteAddr() + RATE_LIMIT_POSTFIX_DELETE;

            // Check if the request is allowed
            if (!rateLimitService.isAllowed(RATE_LIMIT_KEY, MAX_REQUESTS, DURATION, TIME_UNIT)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new CustomHttpResponse(HttpStatus.TOO_MANY_REQUESTS.value(), 
                    "Too many requests", null));
            }

            // Delete department
            departmentService.delete(id);

            // Return response
            return ResponseEntity.status(HttpStatus.OK)
                .body(new CustomHttpResponse(HttpStatus.OK.value(), 
                "Department deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An error occurred while deleting department", e.getMessage()));
        }
    }
}
