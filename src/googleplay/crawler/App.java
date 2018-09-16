/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

/**
 *
 * @author kworkstat2
 */
public class App {

    public App(String name, String id, String category, String mail, String update) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.mail = mail;
        this.update = update;
    }

    public String name;
    public String id;
    public String category;
    public String mail;
    public String update;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

}
