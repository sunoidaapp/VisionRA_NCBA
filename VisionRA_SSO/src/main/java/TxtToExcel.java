
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtToExcel {
    public static void main(String[] args) {
        String inputFile = "E:\\Vulnear\\NCBA\\NCBARA.txt";
        String outputFile = "E:\\Vulnear\\NCBA\\NCBARA1.xlsx";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Vulnerabilities");
            int rowNum = 0;

            String line;
            while ((line = br.readLine()) != null) {
                // Skip borders, empty lines, and the title/header row
                if (line.startsWith("┌") || line.startsWith("├") || line.startsWith("└") || line.trim().isEmpty() || line.contains("Library")) {
                    continue;
                }

                // Split by │ and filter blanks
                String[] parts = line.split("│");
                List<String> cells = new ArrayList<>();
                for (String part : parts) {
                    String cell = part.trim();
                    if (!cell.isEmpty()) {
                        cells.add(cell);
                    }
                }

                // Write actual data rows only
                if (!cells.isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    for (int i = 0; i < cells.size(); i++) {
                        row.createCell(i).setCellValue(cells.get(i));
                    }
                }
            }

            // Autosize columns
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }

            System.out.println("✅ Excel created at: " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
