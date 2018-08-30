/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author MesutKutlu
 */
public class createTables {
     public static String url = "jdbc:sqlite:C://googleplay-crawler/gpcrawler.db";
     public  void createNewTable() {
        // SQLite connection string
        
        // SQL statement for creating a new table
        String sql;
         runSql("CREATE TABLE IF NOT EXISTS categories (\n"
                + "	href text PRIMARY KEY,\n"
                + "	categoryName text NOT NULL \n"
                + ");");
          runSql("CREATE TABLE IF NOT EXISTS apps (\n"
                + "	id text NOT NULL,\n"
                + "	appName text PRIMARY KEY,\n"
                + "	catHref text NOT NULL,\n"
                + "	updateDate date NOT NULL,\n"
                + "	devMail text NOT NULL \n"
                + ");");
    }
     public static void runSql(String query){
         try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
     }
}
