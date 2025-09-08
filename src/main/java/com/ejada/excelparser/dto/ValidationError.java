package com.ejada.excelparser.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    private int rowIndex;
    private String message;
}