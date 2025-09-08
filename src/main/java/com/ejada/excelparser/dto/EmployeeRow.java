package com.ejada.excelparser.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRow {
    private String employeeNumber;
    private String employeeName;
    private String employeeEmail;
    private String mobileNumber;
    private Double salary;
    private String departmentName;
}

