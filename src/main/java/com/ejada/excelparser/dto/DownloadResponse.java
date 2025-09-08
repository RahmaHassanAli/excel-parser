package com.ejada.excelparser.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResponse {
    private String fileName;
    private String base64;
}
