/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

/**
 *
 * @author MesutKutlu
 */
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteJDBC {

    public static String url = "jdbc:sqlite:C://googleplay-crawler/gpcrawler.db";

    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C://googleplay-crawler/gpcrawler.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void addCategoryEntry(String name, String href) {
        if (!checkCatExist(href)) {
            String sql = "INSERT INTO categories(href,categoryName) VALUES(?,?)";

            try (Connection conn = DriverManager.getConnection(url);
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, href);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(name + "already Exist");
        }
    }

    public void addAppEntry(String id, String appName, String catHref, String updateDate, String devMail) {
        System.out.println("id"+id);
        if (!checkEntryExist(id)) {
            String sql = "INSERT INTO apps(id,appName,catHref,updateDate,devMail) VALUES(?,?,?,?,?)";

            try (Connection conn = DriverManager.getConnection(url);
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                System.out.println(id + " | " + appName + " | " + catHref + " | " + updateDate + " | " + devMail);
                Date converted = convertStrToDate(updateDate);
                java.sql.Date sd = new java.sql.Date(converted.getTime());
                System.out.println("TIME:" + sd);
                pstmt.setString(1, id);
                pstmt.setString(2, appName);
                pstmt.setString(3, catHref);
                pstmt.setDate(4, sd);
                pstmt.setString(5, devMail);
                pstmt.executeUpdate();
                pstmt.close();
                //conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(appName + " already exist in database");
        }
    }

    public boolean checkCatExist(String id) {
        String sql = "SELECT COUNT(*) as total FROM categories where href=?";

        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            int mm =  rs.getInt("total");
            rs.close();
            //conn.close();
            pstmt.close();
            return mm > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean checkEntryExist(String id) {
        String sql = "SELECT COUNT(*) as total FROM apps where id=?";

        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            int mm =  rs.getInt("total");
            rs.close();
            //conn.close();
            pstmt.close();
            return mm > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Date convertStrToDate(String dateStr) {
        System.out.println(dateStr);
        String[] splited = dateStr.replace(",", "").split("\\s+");
        String month = "01";
        switch (splited[0]) {
            case "January":
                month = "01";
                break;
            case "February":
                month = "02";
                break;
            case "March":
                month = "03";
                break;
            case "April":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "June":
                month = "06";
                break;
            case "July":
                month = "07";
                break;
            case "August":
                month = "08";
                break;
            case "September":
                month = "09";
                break;
            case "October":
                month = "10";
                break;
            case "Novamber":
                month = "11";
                break;
            case "December":
                month = "12";
                break;
        }
        switch (splited[1]) {
            case "Ocak":
                month = "01";
                break;
            case "Şubat":
                month = "02";
                break;
            case "Mart":
                month = "03";
                break;
            case "Nisan":
                month = "04";
                break;
            case "Mayıs":
                month = "05";
                break;
            case "Haziran":
                month = "06";
                break;
            case "Temmuz":
                month = "07";
                break;
            case "Ağustos":
                month = "08";
                break;
            case "Eylül":
                month = "09";
                break;
            case "Ekim":
                month = "10";
                break;
            case "Kasım":
                month = "11";
                break;
            case "Aralık":
                month = "12";
                break;
        }
        String startDateString;
        if (dateStr.contains(",")) {
            startDateString = splited[2] + "-" + month + "-" + splited[1];

        } else {
            startDateString = splited[2] + "-" + month + "-" + splited[0];

        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date retDate;
        try {
            retDate = df.parse(startDateString);
            return retDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList selectAll(String filter){
        String sql = "SELECT id, appName, catHref, strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch'), devMail FROM Apps ";
        sql += filter;
        System.out.println("SQL: "+ sql);
        ArrayList<String> arr = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // loop through the result set
            while (rs.next()) {
                arr.add(rs.getString("id") +  "*" + 
                                   rs.getString("appName") + "*" +
                                   rs.getString("catHref") + "*" +
                                   rs.getString("strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch')")+ "*" +
                                   rs.getString("devMail"));
            }
            return arr;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
