package com.ejada.excelparser.service;

import com.ejada.excelparser.dto.*;
import com.ejada.excelparser.exception.BadRequestException;
import com.ejada.excelparser.exception.NotFoundException;
import com.ejada.excelparser.util.ExcelParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

@Service
public class ExcelService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public UploadResponse handleUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".xlsx")) {
            throw new BadRequestException("Only .xlsx files are supported");
        }

        ExcelParser.ParseResult parsed;
        try (InputStream in = file.getInputStream()) {
            parsed = ExcelParser.parse(in);
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse Excel: " + e.getMessage());
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + dir, e);
        }

        String cleanName = StringUtils.cleanPath(original);
        Path target = dir.resolve(cleanName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + target, e);
        }

        String status = parsed.errors.isEmpty() ? "success" : "failed";
        return UploadResponse.builder()
                .status(status)
                .validCount(parsed.validRows.size())
                .errors(parsed.errors)
                .fileLocation(target.toString())
                .build();
    }

    public DownloadResponse handleDownload(DownloadRequest req) {
        if (req == null || req.getFilePath() == null || req.getFilePath().isBlank()) {
            throw new BadRequestException("filePath is required");
        }
        Path path = Paths.get(req.getFilePath());
        if (!Files.exists(path)) {
            throw new NotFoundException("File not found: " + req.getFilePath());
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            String b64 = Base64.getEncoder().encodeToString(bytes);
            return DownloadResponse.builder()
                    .fileName(path.getFileName().toString())
                    .base64(b64)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + req.getFilePath(), e);
        }
    }
}
