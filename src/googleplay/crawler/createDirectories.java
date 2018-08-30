/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.io.File;

/**
 *
 * @author MesutKutlu
 */
public class createDirectories {
    public boolean create(){
    String path = "C://googleplay-crawler";

    File directory = new File(path);
    if (! directory.exists()){
        directory.mkdir();
        return true;
    }
    return false;
    }
}

