package com.ejada.excelparser.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String status;
    private int validCount;
    private List<ValidationError> errors;
    private String fileLocation;
}