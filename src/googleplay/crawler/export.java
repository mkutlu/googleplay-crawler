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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author kworkstat2
 */
public class export {
    public void export(ArrayList<App> list){
     HashSet<App> addressesSet = new HashSet<>(list);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("appMails");

                int rowNum = 0;
                System.out.println("Creating excel");

                for (App element : addressesSet) {
                    Row row = sheet.createRow(rowNum++);
                    int colNum = 0;
                    Cell cell = row.createCell(colNum);
                    cell.setCellValue(element.getMail());
                }
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date());


                try {
                    FileOutputStream outputStream = new FileOutputStream(timeStamp+ ".xlsx");
                    workbook.write(outputStream);
                    workbook.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }
}
