/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitests;

/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */

import database.PostgresDBClient;
import database.Researcher;
import database.ResultSetTableModel;
import database.ResultSetTableModelPub;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import researchscraper.ScholarReader;


public class UI extends JFrame {
    private final GridBagLayout layout; // layout to use in panels
    private final GridBagConstraints constraints; // layout's constraints
    private final JPanel panel1; // panel for tab one
    private ResultSetTableModel resultSetTableModel;
    private ResultSetTableModelPub resultSetTableModelPub;
    private JTable researchersTable;
    private JTable publicationsTable;
    public JTextArea jtLogger;
    
    // set up GUI
    public UI() throws SQLException {
        // Frame wide config
        super("Akademia");        
        JTabbedPane tabbedPane =  new JTabbedPane(); // create JTabbedPane
        
        // panel1 config
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        panel1 = new JPanel();        
        panel1.setLayout(layout);
        // creating ui objects
        // JLabels
        JLabel jlblAA = new JLabel("Α/Α", SwingConstants.RIGHT);
        JLabel jlblNameGr = new JLabel("Όνομα", SwingConstants.RIGHT);
        JLabel jlblSurNameGr = new JLabel("Επίθετο", SwingConstants.RIGHT);
        JLabel jlblName = new JLabel("Όνομα Αναζήτησης", SwingConstants.RIGHT);
        JLabel jlblSurName = new JLabel("Επίθετο Αναζήτησης", SwingConstants.RIGHT);
        JLabel jlblEmail = new JLabel("Διεύθυνση Ηλ.Ταχ.", SwingConstants.RIGHT);
        JLabel jlblDate = new JLabel("Ημ/νια Τελ. Ενημέρωσης", SwingConstants.RIGHT);
        // JTextFields
        JTextField jtxtAA = new JTextField(15);
        jtxtAA.setEditable(false);
        JTextField jtxtNameGr = new JTextField(15);
        JTextField jtxtSurNameGr = new JTextField(15);
        JTextField jtxtName = new JTextField(15);
        JTextField jtxtSurName = new JTextField(15);
        JTextField jtxtEmail = new JTextField(15);
        JTextField jtxtDate = new JTextField(15);
        jtxtDate.setEditable(false);
        // JButtons
        JButton jbClear = new JButton("Εκκαθάριση");
        jbClear.addActionListener(
            new ActionListener() { // anonymous inner class
                // handle JButton event and clear all textfields for new entry
                @Override
                public void actionPerformed(ActionEvent event) {
                    jtxtAA.setText("");
                    jtxtNameGr.setText("");
                    jtxtSurNameGr.setText("");
                    jtxtName.setText("");
                    jtxtSurName.setText("");
                    jtxtEmail.setText("");
                    jtxtDate.setText("");
                }
        });
        JButton jbInsert = new JButton("Εισαγωγή");
        jbInsert.addActionListener(
                new ActionListener() { // anonymous inner class
                    // handle JButton event and insert textfields' value in database
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (jtxtNameGr.getText().equals("") || jtxtSurNameGr.getText().equals("") ||
                                jtxtName.getText().equals("") || jtxtSurName.getText().equals("")) {
                            JOptionPane.showMessageDialog(rootPane,
                                    "Τα πεδία 'Όνομα', 'Επίθετο', 'Όνoμα Αναζήτησης', 'Επίθετο Αναζήτησης' "
                                            + "είναι υποχρεωτικά.",
                                    "Μήνυμα", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            PostgresDBClient pDBC = new PostgresDBClient();
                            pDBC.insResearcher(
                                    jtxtNameGr.getText(),
                                    jtxtSurNameGr.getText(),
                                    jtxtName.getText(),
                                    jtxtSurName.getText(),
                                    jtxtEmail.getText()
                            );
                            jtLogger.append("\nSuccessfully inserted " + jtxtName.getText() + " " +
                                    jtxtSurName.getText());
                            jtxtAA.setText("");
                            jtxtNameGr.setText("");
                            jtxtSurNameGr.setText("");
                            jtxtName.setText("");
                            jtxtSurName.setText("");
                            jtxtEmail.setText("");
                            jtxtDate.setText("");
                            
                            try {
                                resultSetTableModel.updateView();
                            } catch (SQLException ex) {
                                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }        
                    }
                }
        );
        JButton jbEdit = new JButton("Επεξεργασία");
        jbEdit.addActionListener(
                new ActionListener() { // anonymous inner class
                    // handle JButton event and edit selected record from JTable
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (researchersTable.getSelectedRow() > -1) {
                            int row = researchersTable.convertRowIndexToModel(researchersTable.getSelectedRow());                        
                            jtxtAA.setText(researchersTable.getModel().getValueAt(row, 0).toString());
                            jtxtNameGr.setText(researchersTable.getModel().getValueAt(row, 1).toString());
                            jtxtSurNameGr.setText(researchersTable.getModel().getValueAt(row, 2).toString());
                            jtxtName.setText(researchersTable.getModel().getValueAt(row, 3).toString());
                            jtxtSurName.setText(researchersTable.getModel().getValueAt(row, 4).toString());
                            jtxtEmail.setText(researchersTable.getModel().getValueAt(row, 5).toString());
                            try {
                                jtxtDate.setText(researchersTable.getModel().getValueAt(row, 6).toString());
                            }
                            catch (Exception exception) {
                                jtxtDate.setText("");
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(rootPane, "Επιλέξτε εγγραφή για επεξεργασία",
                                    "Μήνυμα",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
        );
        JButton jbSave = new JButton("Αποθήκευση");
        jbSave.addActionListener(
                new ActionListener() { // anonymous inner class
                    @Override
                    // handle JButton event and 
                    public void actionPerformed(ActionEvent event) {
                        if (jtxtAA.getText().equals("") || jtxtNameGr.getText().equals("") || jtxtSurNameGr.getText().equals("") ||
                                jtxtName.getText().equals("") || jtxtSurName.getText().equals("")) {
                            JOptionPane.showMessageDialog(rootPane,
                                    "Επιλέξτε εγγραφή για επεξεργασία.",
                                    "Μήνυμα", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            PostgresDBClient pDBC = new PostgresDBClient();
                            pDBC.updResearcher(
                                    Integer.parseInt(jtxtAA.getText()),
                                    jtxtNameGr.getText(),
                                    jtxtSurNameGr.getText(),
                                    jtxtName.getText(),
                                    jtxtSurName.getText(),
                                    jtxtEmail.getText()
                            );
                            jtLogger.append("\nSuccessfully saved " + jtxtName.getText() + " " +
                                    jtxtSurName.getText());
                            jtxtAA.setText("");
                            jtxtNameGr.setText("");
                            jtxtSurNameGr.setText("");
                            jtxtName.setText("");
                            jtxtSurName.setText("");
                            jtxtEmail.setText("");
                            jtxtDate.setText("");

                            try {
                                resultSetTableModel.updateView();
                            } catch (SQLException ex) {
                                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }    
                    }
                }
        );
        JButton jbDelete = new JButton("Διαγραφή");
        jbDelete.addActionListener(
                new ActionListener() { // anonymous inner class
                    // handle JButton event and delete selected record from JTable/database
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (researchersTable.getSelectedRow() > -1) {
                            int row = researchersTable.convertRowIndexToModel(researchersTable.getSelectedRow());                        
                            int researcher_id = Integer.parseInt(researchersTable.getModel().getValueAt(row, 0).toString());
                            String nameGr = researchersTable.getModel().getValueAt(row, 1).toString();
                            String surNameGr = researchersTable.getModel().getValueAt(row, 2).toString();
                            int response = JOptionPane.showConfirmDialog(rootPane,
                                    "Θέλετε να διαγράψετε την εγγραφή '" + nameGr + " "
                                    + surNameGr + " και όλες τις σχετικές δημοσιεύσεις;",
                                    "Επιβεβαίωση διαγραφής",JOptionPane.OK_CANCEL_OPTION);
                            if (response==0) {
                                PostgresDBClient pDBC =  new PostgresDBClient();
                                pDBC.delResearcher(researcher_id);
                                jtLogger.append("\nSuccessfully Deleted " + nameGr + " " +surNameGr);
                                
                                try {
                                    resultSetTableModel.updateView();
                                }
                                catch (SQLException ex) {
                                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        else 
                            JOptionPane.showMessageDialog(rootPane, "Επιλέξτε εγγραφή για διαγραφή",
                                    "Μήνυμα", JOptionPane.PLAIN_MESSAGE);
                    }
                }
        );
        JButton jbMultiUpdate = new JButton("Μαζική ενημέρωση");
        jbMultiUpdate.addActionListener(
                new ActionListener() { // anonymous inner class
                    // handle JButton event and update ALL publications for ALL researchers
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        int response = JOptionPane.showConfirmDialog(rootPane,
                                "Η ενημέρωση όλων των δημοσιεύσεων όλων των ερευνητών ενδέχεται να προκαλέσει\n" +
                                "σφάλμα και αποκλεισμού της IP διεύθυνσης σας λόγω των συστημάτων αυτοπροστασίας\n " +
                                "που διαθέτουν οι σύγχρονες μηχανές αναζήτησης. Επιθυμείτε να συνεχίσετε;",
                                "Επιβεβαίωση Μαζικής Ενημέρωσης",JOptionPane.OK_CANCEL_OPTION);
                        if (response == 0) {
                            jtLogger.append("\nInitiating Multiple Researcher Update");
                            for (int i = 0;i<researchersTable.getModel().getRowCount();i++) {
                                ScrapeCoordinator(researchersTable.getModel().getValueAt(i, 0).toString());
                            }
                        }
                        
                    }
                }
        );
        JButton jbSingleUpdate = new JButton("Μεμονωμένη Ενημέρωση");
        jbSingleUpdate.addActionListener(
                new ActionListener() { // anonymous inner class
                    // handle JButton event and update ALL publications for ALL researchers
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (researchersTable.getSelectedRow() > -1) {
                            int row = researchersTable.convertRowIndexToModel(researchersTable.getSelectedRow());
                            jtLogger.append("\nInitiating SingleUpdate");
                            ScrapeCoordinator(researchersTable.getModel().getValueAt(row, 0).toString());
                        }
                        else {
                            JOptionPane.showMessageDialog(rootPane, "Επιλέξτε εγγραφή για ενημέρωση",
                                    "Μήνυμα", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
        );
        JButton jbMSMultiUpdate = new JButton("Μαζική ενημέρωση");
        JButton jbMSSingleUpdate = new JButton("Μεμονωμένη Ενημέρωση");
        // Box
        Box updScholarBox = Box.createHorizontalBox();        
        updScholarBox.add(Box.createHorizontalGlue());
        updScholarBox.add(jbMultiUpdate);
        updScholarBox.add(Box.createHorizontalGlue());
        updScholarBox.add(jbSingleUpdate);
        updScholarBox.add(Box.createHorizontalGlue());
        updScholarBox.setBorder(BorderFactory.createTitledBorder(null, "Google Scholar",TitledBorder.LEFT, TitledBorder.ABOVE_TOP));
        
        Box updAcademicBox = Box.createHorizontalBox();        
        updAcademicBox.add(Box.createHorizontalGlue());
        updAcademicBox.add(jbMSMultiUpdate);
        updAcademicBox.add(Box.createHorizontalGlue());
        updAcademicBox.add(jbMSSingleUpdate);
        updAcademicBox.add(Box.createHorizontalGlue());
        updAcademicBox.setBorder(BorderFactory.createTitledBorder(null, "Microsoft Acadmemic",TitledBorder.LEFT, TitledBorder.ABOVE_TOP));
        // JTable
        resultSetTableModel = new ResultSetTableModel();
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(resultSetTableModel);
        researchersTable = new JTable(resultSetTableModel);
        researchersTable.setRowSorter(sorter);
        researchersTable.getTableHeader().setReorderingAllowed(false);
        // JTextArea
        jtLogger = new JTextArea("Welcome to ResearchScraper!!!");
        // Position ui objects in the layout
        // weightx and weighty are 0 : the default        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(jlblAA, 0, 0, 2, 1);
        addComponent(jlblNameGr, 1, 0, 2, 1);
        addComponent(jlblSurNameGr, 2, 0, 2, 1);
        addComponent(jlblName, 3, 0, 2, 1);
        addComponent(jlblSurName, 4, 0, 2, 1);
        addComponent(jlblEmail, 5, 0, 2, 1);
        addComponent(jlblDate, 6, 0, 2, 1);
        constraints.weightx = 1;
        constraints.weighty = 1;
        addComponent(jtxtAA, 0, 2, 2, 1);
        addComponent(jtxtNameGr, 1, 2, 2, 1);
        addComponent(jtxtSurNameGr, 2, 2, 2, 1);
        addComponent(jtxtName, 3, 2, 2, 1);
        addComponent(jtxtSurName, 4, 2, 2, 1);
        addComponent(jtxtEmail, 5, 2, 2, 1);
        addComponent(jtxtDate, 6, 2, 2, 1);
        //constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        addComponent(jbClear, 0, 4, 2, 1);
        addComponent(jbInsert, 1, 4, 2, 1);
        addComponent(jbEdit, 3, 4, 2, 1);
        addComponent(jbSave, 4, 4, 2, 1);
        addComponent(jbDelete, 6, 4, 2, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        addComponent(updScholarBox, 7, 0, 3, 1);
        addComponent(updAcademicBox, 7, 3, 3, 1);        
        constraints.weightx = 100;
        constraints.weighty = 100;
        addComponent(new JScrollPane(researchersTable), 9, 0, 6,3);
        addComponent(new JScrollPane(jtLogger), 12, 0, 6, 4);
        
        tabbedPane.addTab("Εισαγωγή", null, panel1, "Διαχείριση εισαγωγών");
        
        //////////////////////////   Panel 2  //////////////////////////////////////////
        // set up panel2 and add it to JTabbedPane
        JPanel panel2 = new JPanel();
        BorderLayout borderLayout = new BorderLayout(5,5);
        panel2.setLayout(borderLayout);
        // JLabel
        JLabel label2 = new JLabel("Προβολή αποθηκευμένων δημοσιεύσεων από τη βάση.", SwingConstants.CENTER);
        // JComboBox
        PostgresDBClient pDBC = new PostgresDBClient();
        ArrayList<Researcher> dbResult = pDBC.selResearher();
        JComboBox<String> jcResearcherMenu = new JComboBox<String>();
        for (int i = 0; i < dbResult.size(); i++) {
            jcResearcherMenu.addItem("<" +
                    Integer.toString(dbResult.get(i).getResearcherID()) + ">" +
                    dbResult.get(i).getSurNameGr() + " " +
                    dbResult.get(i).getNameGr()
            );
        }
        jcResearcherMenu.addItemListener(
                new ItemListener() {// anonymous inner class
                    // handle JComboBox event and alter JTextArea contents
                    @Override
                    public void itemStateChanged(ItemEvent event) {
                        if (event.getStateChange() == ItemEvent.SELECTED) {
                            String choice = jcResearcherMenu.getSelectedItem().toString();
                            int endOfID = choice.indexOf(">");
                            String researcher_id = choice.substring(1,endOfID);
                            System.out.println(researcher_id);
                            try {
                                resultSetTableModelPub.updateView(Integer.parseInt(researcher_id));
                            } catch (SQLException ex) {
                                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        }
                    }
                }
        );
        // JTable
        resultSetTableModelPub = new ResultSetTableModelPub(-1);
        final TableRowSorter<TableModel> sorterPub = new TableRowSorter<TableModel>(resultSetTableModelPub);
        publicationsTable = new JTable(resultSetTableModelPub);
        publicationsTable.setRowSorter(sorterPub);
        //publicationsTable.getTableHeader().setReorderingAllowed(false);
        // Box
        Box menuBox = Box.createVerticalBox();
        menuBox.add(Box.createVerticalGlue());
        menuBox.add(label2);
        menuBox.add(Box.createVerticalGlue());
        menuBox.add(jcResearcherMenu);
        panel2.add(menuBox, BorderLayout.NORTH);
        panel2.add(new JScrollPane(publicationsTable), BorderLayout.CENTER);
        tabbedPane.addTab("Προβολη", null, panel2, "Προβολή καταγεγραμμένων δημοσιεύσεων");
        
        add(tabbedPane); // add JTabbedPane to frame        
    }
    
    // Utility method that takes care of component place in the GridBagLayout
    // Courtesy of P & H Deitel
    private void addComponent(Component component, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(component, constraints);
        panel1.add(component);
    }
    
    // creates a SchcolarReader object and initiates scrape
    private void ScrapeCoordinator(String id) {
        try {
            String researcher_id = id;
            ScholarReader sr = new ScholarReader(researcher_id, "", "");
            
            int pages = 0;

            if (sr.resultsExist()) {
                System.out.println("We have results!");
                jtLogger.append("\nWe have results!");
            }
            else {
                System.out.println("Your search came up with nothing");
                JOptionPane.showMessageDialog(rootPane, "Δεν βρέθηκαν αποτελέσματα.\n"
                        + "Τροποποιήστε τα στοιχεία αναζήτησης." + researcher_id,
                        "Μήνυμα", JOptionPane.INFORMATION_MESSAGE);
                jtLogger.append("\nYour search came up with nothing");
            }

            if (sr.resultsExist()) {
                pages = sr.numOfResultPages();
                if (pages == -2) pages = 0;
                System.out.println("There are a total of " + pages + " result pages.");
                jtLogger.append("\nThere are a total of " + pages + " result pages.");

                for (int i = 0;i<(pages*10);i = i + 10) {
                    System.out.println("You are on page: " + (i/10+1));
                    jtLogger.append("\nYou are on page: " + (i/10+1));
                    //sr.getResults(i);
                    System.out.printf("\n\n");
                }
                sr.updateResearcher(Integer.parseInt(researcher_id));
                jtLogger.append("\nSuccessfully updated researcher " + researcher_id);
                JOptionPane.showMessageDialog(rootPane, "Επιτυχής ενημέρωσης ερευνητή " + researcher_id,
                        "Μήνυμα", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception exception) {            
            System.out.println(exception.toString());
        }
        
        try {
            resultSetTableModel.updateView();
        } catch (SQLException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
} // end class UI 
