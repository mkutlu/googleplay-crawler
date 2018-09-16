/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package googleplay.crawler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 *
 * @author MesutKutlu
 */
public class main extends javax.swing.JFrame {

    /**
     * Creates new form main
     */
    public static DefaultTableModel model;

    public main(String callCode) {
        if (callCode.equals("main")) {
            initComponents();
            jXDatePicker2.setEnabled(false);
            jXDatePicker1.setEnabled(false);

            UtilDateModel model = new UtilDateModel();
            Properties p = new Properties();
            p.put("text.today", "Today");
            p.put("text.month", "Month");
            p.put("text.year", "Year");
            JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            jPanel1.add(datePicker);

            // create directories in c
            createDirectories cd = new createDirectories();
            if (!cd.create()) {
                createTables ct = new createTables();
                ct.createNewTable();
            }
            DefaultListModel listModel = new DefaultListModel();
            listModel.addElement("Ana Sayfa : /store/apps");
            listModel.addElement("En iyi Uygulamalar : /store/apps/top");
            listModel.addElement("Yeni çıkanlar : /store/apps/new");
            getCategories gc = new getCategories();
            try {
                ArrayList<String> arr = gc.getAllCategories();
                arr.forEach(element -> listModel.addElement(element));
                catList.setModel(listModel);
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
            ((DefaultComboBoxModel) jComboBox1.getModel()).addElement("");

            populateTable("");

        }
    }

    public void getAllApps(String href) throws IOException {
        Document document = Jsoup.connect("https://play.google.com" + href).get();
        Elements categories = document.select(".see-more");
        if (categories.isEmpty()) {
            System.out.println(href + " CRAWLING :");
            runPhantomjsWebDriver(href);
        } else {
            categories.forEach(category -> {
                System.out.println(category.attr("href") + " CRAWLING :");
                runPhantomjsWebDriver(category.attr("href"));
            });
        }
    }

    public void runPhantomjsWebDriver(String href) {
        try {
            // Set executable file path to system variable phantomjs.binary.path's value.
            String phantomjsExeutableFilePath = "lib/phantomjs-2.1.1-windows/bin/phantomjs.exe";
            System.setProperty("phantomjs.binary.path", phantomjsExeutableFilePath);
            // Initiate PhantomJSDriver.
            WebDriver driver = new PhantomJSDriver();
            //Must make the web browser full size other wise it can not parse out result by xpath.
            driver.manage().window().maximize();
            driver.get("https://play.google.com" + href);
            scrollToBottom(driver, null, 4000);
            Thread.sleep(6000);
            // Print out yahoo home page title.
            System.out.println("Page title is: " + driver.getTitle());
            jLabel7.setText(href + " page crawl started...");

            By resultListByXPath = By.xpath(".//div[contains(@class,'card')]/.//div[contains(@class,'reason-set')]/.//a");
            List resultElementList = driver.findElements(resultListByXPath);

            if (resultElementList != null) {
                int size = resultElementList.size();
                System.out.println("Search result list size = " + size);
                // Loop the result list.
                for (int i = 0; i < size; i++) {
                    WebElement resultElement = (WebElement) resultElementList.get(i);

                    try {
                        // Get result item title element by xpath.
                        crawlApp(resultElement.getAttribute("href"));
                    } catch (NoSuchElementException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            jLabel7.setText(href + " page crawl started...");

            driver.quit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized WebDriver scrollToBottom(WebDriver driver, WebElement element, int time) throws InterruptedException {
        jLabel7.setText("Browser scroll start...");
        String oldpage = "";
        String newpage = "";

        do {
            By submitBtnById = By.id("show-more-button");
            WebElement submitBtn = driver.findElement(submitBtnById);
            if (submitBtn != null && submitBtn.isDisplayed()) {
                submitBtn.click();
                System.out.println("More button clicked");
                Thread.sleep(3000);
            }
            oldpage = driver.getPageSource();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, (document.body.scrollHeight))");
            this.wait(time);
            newpage = driver.getPageSource();
        } while (!oldpage.equals(newpage));
        jLabel7.setText("Browser scroll done!");

        return driver;
    }

    public void crawlApp(String appHref) {
        try {
            String name, update = "", mail = "", id = "", category = "";
            Document document = Jsoup.connect(appHref).get();

            Elements nameTag = document.select(".AHFaub span");
            name = nameTag.text();

            Elements catTag = document.select("a");
            for (Element tag : catTag) {
                if (tag.attr("itemprop").equals("genre")) {
                    category = tag.text();
                }
            }
            id = appHref.substring(appHref.indexOf("=") + 1);

            Elements aTags = document.select(".htlgb a");
            for (Element tag : aTags) {
                if (tag.text().contains("@")) {
                    mail = tag.text();
                }
            }

            Elements spanTags = document.select(".htlgb span");
            for (Element tag : spanTags) {
                String[] spl = tag.text().replace(",", "").split("\\s+");
                if (spl.length == 3) {
                    if ((spl[0].matches("\\d+") && spl[2].matches("\\d+") || (spl[1].matches("\\d+") && spl[2].matches("\\d+")))) {
                        update = tag.text();
                    }
                }
            }
            System.out.println("Eklenen id: " + id + " , name: " + name + " , category: " + category + " , update: " + update + " , mail: " + mail + " ");

            SQLiteJDBC sql = new SQLiteJDBC();
            sql.addAppEntry(id, name, category, update, mail);
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.addRow(new Object[]{name, category, mail, update, id});
            //this.repaint();

        } catch (IOException ex) {
        }
    }

    public void populateTable(String filter) {
        //right table populate
        SQLiteJDBC sq = new SQLiteJDBC();
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        if (model.getRowCount() > 0) {
            for (int i = model.getRowCount() - 1; i > -1; i--) {
                model.removeRow(i);
            }
        }
        ArrayList<String> array = sq.selectAll(filter);
        int count = 0;
        for (String element : array) {
            count++;
            String[] arr = element.split("\\*");
            model.addRow(new Object[]{count, arr[1], arr[2], arr[4], arr[3], arr[0]});
            if (((DefaultComboBoxModel) jComboBox1.getModel()).getIndexOf(arr[2]) == -1) {
                ((DefaultComboBoxModel) jComboBox1.getModel()).addElement(arr[2]);
            }
        }
        jTable2.setAutoCreateRowSorter(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        catList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        submitBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel4 = new javax.swing.JLabel();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        button1 = new java.awt.Button();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Google Play Store Crawler");
        setPreferredSize(new java.awt.Dimension(1600, 650));

        jTabbedPane1.setFont(new java.awt.Font("MS UI Gothic", 1, 14)); // NOI18N

        jScrollPane1.setViewportView(catList);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "App Name", "Category", "Mail", "Last Update", "id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setToolTipText("");
        jTable1.setRequestFocusEnabled(false);
        jScrollPane3.setViewportView(jTable1);

        submitBtn.setText("Crawl Data");
        submitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitBtnActionPerformed(evt);
            }
        });

        jButton1.setText("Export Table");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setText("Status: Ready");
        jLabel7.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(submitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(182, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(submitBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Crawl", jPanel1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "App Name", "Category", "Mail", "Last Update", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setToolTipText("");
        jTable2.setRequestFocusEnabled(false);
        jScrollPane4.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Filter");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel2.setText("App Name:");

        jLabel3.setText("Category:");

        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Date: (From)");

        jXDatePicker2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Date: (To)");

        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setText("Export");

        button1.setLabel("Export Table to XLSX");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        jComboBox1.setToolTipText("");
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel8.setText("Status: Ready");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48))
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                            .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jXDatePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(93, 93, 93))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 24, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)))
                        .addGap(372, 372, 372))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Database/Export", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1647, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void submitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitBtnActionPerformed
        jLabel7.setText("Data crawl start...");
        Thread thread = new Thread() {
            public void run() {
                catList.getSelectedValuesList().forEach(element -> {
                    String[] arr = element.split(":");
                    try {
                        getAllApps(arr[1].substring(1));
                    } catch (IOException ex) {
                        Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                jLabel7.setText("Data crawl done!");
            }
        };
        thread.start();
    }//GEN-LAST:event_submitBtnActionPerformed
    private void jXDatePicker2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker2ActionPerformed
        populateFunc();
    }//GEN-LAST:event_jXDatePicker2ActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        jLabel8.setText("Data export start...");
        export exp = new export();
        ArrayList<App> list = new ArrayList<>();
        TableModel dataModel = jTable2.getModel();
        for (int row = 0; row < dataModel.getRowCount(); row++) {
            App app = new App(dataModel.getValueAt(row, 1).toString(), dataModel.getValueAt(row, 5).toString(), dataModel.getValueAt(row, 2).toString(),
                    dataModel.getValueAt(row, 3).toString(), dataModel.getValueAt(row, 4).toString());
            list.add(app);
        }
        exp.export(list);
        jLabel8.setText("Data export done!");
    }//GEN-LAST:event_button1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        if (jCheckBox2.isSelected()) {
            jXDatePicker1.setEnabled(true);
        } else {
            jXDatePicker1.setEnabled(false);
            jXDatePicker1.setDate(null);
            populateFunc();
        }
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            jXDatePicker2.setEnabled(true);
        } else {
            jXDatePicker2.setEnabled(false);
            jXDatePicker2.setDate(null);
            populateFunc();
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        DateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String filter = "";
        if (!(evt.getKeyChar() == 27 || evt.getKeyChar() == 65535))//this section will execute only when user is editing the JTextField
        {
            int count = 0;
            if (!jTextField1.getText().equals("")) {
                filter += "WHERE appNamE LIKE '%" + jTextField1.getText() + "%' ";
                count++;
            }
            if (!jComboBox1.getSelectedItem().toString().equals("")) {
                if (count == 0) {
                    filter += "WHERE ";
                }
                if (count > 0) {
                    filter += "AND ";
                }
                filter += " catHref LIKE '%" + jComboBox1.getSelectedItem().toString() + "%' ";
                count++;
            }
            if (jCheckBox2.isSelected() && !jXDatePicker1.getDate().toString().equals("")) {
                if (count == 0) {
                    filter += "WHERE ";
                }
                if (count > 0) {
                    filter += "AND ";
                }
                filter += "strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch') >= Datetime('" + oDateFormat.format(jXDatePicker1.getDate()) + "','-1 day') ";
                count++;
            }
            if (jCheckBox1.isSelected() && !jXDatePicker2.getDate().toString().equals("")) {
                if (count == 0) {
                    filter += "WHERE ";
                }
                if (count > 0) {
                    filter += "AND ";
                }
                filter += "strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch') <= Datetime('" + oDateFormat.format(jXDatePicker2.getDate()) + "') ";
            }
        }
        populateTable(filter);
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jXDatePicker1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker1ActionPerformed
        populateFunc();
    }//GEN-LAST:event_jXDatePicker1ActionPerformed
    public void populateFunc() {
        DateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String filter = "";

        int count = 0;
        if (!jTextField1.getText().equals("")) {
            filter += "WHERE appNamE LIKE '%" + jTextField1.getText() + "%' ";
            count++;
        }
        if (!jComboBox1.getSelectedItem().toString().equals("")) {
            if (count == 0) {
                filter += "WHERE ";
            }
            if (count > 0) {
                filter += "AND ";
            }
            filter += " catHref LIKE '%" + jComboBox1.getSelectedItem().toString() + "%' ";
            count++;
        }
        if (jCheckBox2.isSelected() && !jXDatePicker1.getDate().toString().equals("")) {
            if (count == 0) {
                filter += "WHERE ";
            }
            if (count > 0) {
                filter += "AND ";
            }
            filter += "strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch') >= Datetime('" + oDateFormat.format(jXDatePicker1.getDate()) + "','-1 day') ";
            count++;
        }
        if (jCheckBox1.isSelected() && !jXDatePicker2.getDate().toString().equals("")) {
            if (count == 0) {
                filter += "WHERE ";
            }
            if (count > 0) {
                filter += "AND ";
            }
            filter += "strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch') <= Datetime('" + oDateFormat.format(jXDatePicker2.getDate()) + "') ";
        }

        populateTable(filter);
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jLabel7.setText("Data export start...");
        export exp = new export();
        ArrayList<App> list = new ArrayList<>();
        TableModel dataModel = jTable1.getModel();
        for (int row = 0; row < dataModel.getRowCount(); row++) {
            App app = new App("0", dataModel.getValueAt(row, 4).toString(), dataModel.getValueAt(row, 1).toString(),
                    dataModel.getValueAt(row, 2).toString(), dataModel.getValueAt(row, 3).toString());
            list.add(app);
        }
        exp.export(list);
        jLabel7.setText("Data export done!");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        DateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String filter = "";

        int count = 0;
        if (!jTextField1.getText().equals("")) {
            filter += "WHERE appNamE LIKE '%" + jTextField1.getText() + "%' ";
            count++;
        }
        if (!jComboBox1.getSelectedItem().toString().equals("")) {
            if (count == 0) {
                filter += "WHERE ";
            }
            if (count == 1) {
                filter += "AND ";
            }
            filter += " catHref LIKE '%" + jComboBox1.getSelectedItem().toString() + "%' ";
            count++;
        }
        if (jCheckBox2.isSelected() && !jXDatePicker1.getDate().toString().equals("")) {
            if (count == 0) {
                filter += "WHERE ";
            }
            if (count == 1) {
                filter += "AND ";
            }
            filter += "strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch') >= Datetime('" + oDateFormat.format(jXDatePicker1.getDate()) + "','-1 day') ";
            count++;
        }
        if (jCheckBox1.isSelected() && !jXDatePicker2.getDate().toString().equals("")) {
            if (count == 0) {
                filter += "WHERE ";
            }
            if (count == 1) {
                filter += "AND ";
            }
            filter += "strftime('%Y-%m-%d', updateDate / 1000, 'unixepoch') <= Datetime('" + oDateFormat.format(jXDatePicker2.getDate()) + "') ";
        }

        populateTable(filter);
    }//GEN-LAST:event_jComboBox1ActionPerformed
    public static void changeTable(Object[] element) {
        model.addRow(element);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main("main").setVisible(true);

            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private javax.swing.JList<String> catList;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JButton submitBtn;
    // End of variables declaration//GEN-END:variables
}
