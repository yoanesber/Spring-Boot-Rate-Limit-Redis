package com.yoanesber.rate_limit_with_redis.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.yoanesber.rate_limit_with_redis.dto.SaveDepartmentRequestDTO;
import com.yoanesber.rate_limit_with_redis.dto.UpdateDepartmentRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "department", 
       uniqueConstraints = @UniqueConstraint(name = "idx_16979_dept_name", columnNames = "dept_name"))
public class Department {

    @Id
    @Column(length = 4, nullable = false)
    private String id;

    @Column(name = "dept_name", length = 40, nullable = false, unique = true)
    private String deptName;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    public Department(SaveDepartmentRequestDTO departmentDTO) {
        this.id = departmentDTO.getId();
        this.deptName = departmentDTO.getDeptName();
        this.active = departmentDTO.isActive();
        this.createdBy = departmentDTO.getCreatedBy();
        this.createdDate = departmentDTO.getCreatedDate();
        this.updatedBy = departmentDTO.getUpdatedBy();
        this.updatedDate = departmentDTO.getUpdatedDate();
    }

    public Department(UpdateDepartmentRequestDTO departmentDTO) {
        this.deptName = departmentDTO.getDeptName();
        this.active = departmentDTO.isActive();
        this.updatedBy = departmentDTO.getUpdatedBy();
        this.updatedDate = departmentDTO.getUpdatedDate();
    } 
}
