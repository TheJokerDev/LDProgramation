package me.j0keer.excel;

import me.j0keer.LDProgramation;
import me.j0keer.types.Day;
import me.j0keer.types.Person;
import me.j0keer.types.Place;
import me.j0keer.types.Turn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ExcelUtilities {
    private final LDProgramation plugin;
    private File excelFile;

    public ExcelUtilities(LDProgramation plugin) {
        this.plugin = plugin;
        this.excelFile = new File(plugin.getDataFolder(), "temp.xlsx");
    }

    public ExcelUtilities setFile(File file) {
        this.excelFile = file;
        return this;
    }

    public void save() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Persons");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        int rowNumber = 0;
        for (Integer week : plugin.getDateManager().getDays().keySet()) {
            // Create Week row
            Row weekRow = sheet.createRow(rowNumber++);
            Cell weekCell = weekRow.createCell(0);
            weekCell.setCellValue("WEEK " + week);
            weekCell.setCellStyle(headerStyle);

            // Merge cells for week header
            sheet.addMergedRegion(new CellRangeAddress(rowNumber - 1, rowNumber - 1, 0, 4));

            // Create headers for places
            Row headerRow = sheet.createRow(rowNumber++);
            int placeColumnIndex = 1;
            for (Place place : plugin.getPlaceManager().getPlaces().values()) {
                Cell placeCell = headerRow.createCell(placeColumnIndex++);
                placeCell.setCellValue(place.getName());
            }

            for (Day day : plugin.getDateManager().getDays().get(week)) {
                // Create Day row
                Row dayRow = sheet.createRow(rowNumber++);
                Cell dayCell = dayRow.createCell(0);
                dayCell.setCellValue(day.getName()); // Assuming day.getDayName() returns the day's name

                // Iterate over turns
                HashMap<Place, HashMap<Turn, List<Person>>> availability = day.getAvailability();
                for (Turn turn : plugin.getTurnManager().getTurns().values()) {
                    Row turnRow = sheet.createRow(rowNumber++);
                    boolean hasData = false;
                    for (Place place : plugin.getPlaceManager().getPlaces().values()) {
                        for (Turn turn2 : availability.get(place).keySet()) {
                            if (!availability.get(place).get(turn2).isEmpty()) {
                                
                                hasData = true;
                            }
                        }
                    }
                    if (!hasData) continue;

                    Cell turnCell = turnRow.createCell(0);
                    turnCell.setCellValue(turn.name());

                    int cellNumber = 1;
                    for (Place place : plugin.getPlaceManager().getPlaces().values()) {
                        StringBuilder persons = new StringBuilder();
                        if (availability.containsKey(place) && availability.get(place).containsKey(turn)) {
                            for (Person person : availability.get(place).get(turn)) {
                                persons.append(person.getName()).append(", ");
                            }
                        }
                        if (!persons.isEmpty()) {
                            persons.setLength(persons.length() - 2); // Remove last comma and space
                        }
                        Cell turnPlaceCell = turnRow.createCell(cellNumber++);
                        turnPlaceCell.setCellValue(persons.toString());
                    }
                }
            }
        }

        //Save all persons loaded
        Sheet sheetPersons = workbook.createSheet("All Persons");
        rowNumber = 0;
        for (Person value : plugin.getPersonManager().getPersons().values()) {
            Row row = sheetPersons.createRow(rowNumber++);
            Cell cell = row.createCell(0);
            cell.setCellValue(value.toString());
        }

        // Adjust column widths
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(excelFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            plugin.console("{prefix}&cError creating Excel file. Please check the console for more information.");
            plugin.console("{prefix}&cError: " + e.getMessage());
            return;
        }
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        plugin.console("{prefix}&aExcel file has been created!");
    }
}
