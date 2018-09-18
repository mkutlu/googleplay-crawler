/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author kworkstat2
 */
public class export {

    public void export(ArrayList<App> list) {
        HashSet<App> addressesSet = new HashSet<>(list);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("appMails");

        int rowNum = 0;
        System.out.println("Creating excel");
        ArrayList<String> arr = new ArrayList<>();
        for (App element : addressesSet) {
            if (!arr.contains(element.getMail())) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(element.getName());
                row.createCell(1).setCellValue(element.getMail());
                row.createCell(2).setCellValue(element.getCategory());
                row.createCell(3).setCellValue(element.getUpdate());
                row.createCell(4).setCellValue(element.getId());
                arr.add(element.getMail());
            }

        }
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date());
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(2);

        try {
            FileOutputStream outputStream = new FileOutputStream(timeStamp + ".xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
