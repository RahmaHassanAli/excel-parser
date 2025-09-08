package com.ejada.excelparser.controller;


import com.ejada.excelparser.dto.*;
import com.ejada.excelparser.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        return excelService.handleUpload(file);
    }


    @PostMapping(value = "/download", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponse download(@RequestBody DownloadRequest req) {
        return excelService.handleDownload(req);
    }

}
