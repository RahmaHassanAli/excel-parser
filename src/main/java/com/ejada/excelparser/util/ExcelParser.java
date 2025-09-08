package com.ejada.excelparser.util;

import com.ejada.excelparser.dto.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;


public class ExcelParser {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern MOBILE = Pattern.compile("^\\+?[0-9]{10,15}$");

    public static class ParseResult {
        public final List<EmployeeRow> validRows = new ArrayList<>();
        public final List<ValidationError> errors = new ArrayList<>();
    }


    public static ParseResult parse(InputStream in) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            ParseResult result = new ParseResult();

            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                result.errors.add(ValidationError.builder()
                        .rowIndex(1)
                        .message("Sheet is empty")
                        .build());
                return result;
            }

            Row header = sheet.getRow(0);
            String[] expected = {
                    "Employee Number", "Employee Name", "Employee Email",
                    "Mobile Number", "Salary", "Department Name"
            };

            for (int i = 0; i < expected.length; i++) {
                String hv = getStringCell(header, i);
                if (hv == null || !hv.trim().equalsIgnoreCase(expected[i])) {
                    result.errors.add(ValidationError.builder()
                            .rowIndex(1)
                            .message("Invalid header at column " + (i+1) + ": expected '" + expected[i] + "'")
                            .build());
                }
            }
            if (!result.errors.isEmpty()) return result;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                int visibleRowNum = r + 1;

                String empNo = getStringCell(row, 0);
                String name = getStringCell(row, 1);
                String email = getStringCell(row, 2);
                String mobile = getStringCell(row, 3);
                Double salary = getNumericCell(row, 4);
                String dept = getStringCell(row, 5);

                List<String> rowErrors = new ArrayList<>();

                if (isBlank(empNo)) rowErrors.add("Employee Number is required");
                if (isBlank(name)) rowErrors.add("Employee Name is required");
                if (isBlank(email)) rowErrors.add("Employee Email is required");
                if (isBlank(mobile)) rowErrors.add("Mobile Number is required");
                if (salary == null) rowErrors.add("Salary is required and must be a number");
                if (isBlank(dept)) rowErrors.add("Department Name is required");

                if (!isBlank(email) && !EMAIL.matcher(email).matches())
                    rowErrors.add("Invalid email format");

                if (!isBlank(mobile) && !MOBILE.matcher(mobile).matches())
                    rowErrors.add("Invalid mobile format (use digits, optional leading +, 10–15 length)");

                if (salary != null && salary < 0)
                    rowErrors.add("Salary must be non-negative");

                if (!rowErrors.isEmpty()) {
                    result.errors.add(ValidationError.builder()
                            .rowIndex(visibleRowNum)
                            .message(String.join("; ", rowErrors))
                            .build());
                    continue;
                }

                result.validRows.add(EmployeeRow.builder()
                        .employeeNumber(empNo)
                        .employeeName(name)
                        .employeeEmail(email)
                        .mobileNumber(mobile)
                        .salary(salary)
                        .departmentName(dept)
                        .build());
            }

            return result;
        }
    }

    private static String getStringCell(Row row, int col) {
        Cell c = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return null;
        if (c.getCellType() == CellType.STRING) return c.getStringCellValue();
        if (c.getCellType() == CellType.NUMERIC) {
            double v = c.getNumericCellValue();
            long asLong = (long) v;
            if (asLong == v) return String.valueOf(asLong);
            return String.valueOf(v);
        }
        if (c.getCellType() == CellType.BOOLEAN) return String.valueOf(c.getBooleanCellValue());
        return null;
    }

    private static Double getNumericCell(Row row, int col) {
        Cell c = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return null;
        if (c.getCellType() == CellType.NUMERIC) return c.getNumericCellValue();
        if (c.getCellType() == CellType.STRING) {
            try { return Double.parseDouble(c.getStringCellValue().trim()); } catch (Exception ignored) {}
        }
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
