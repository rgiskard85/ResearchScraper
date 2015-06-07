/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */
public class PostgresDBClient {
    private static final String dbUrl = "jdbc:postgresql://83.212.122.8:5432/postgres";
    private static final String dbUser = "akademia";
    private static final String dbPass = "ds525";
    
    private Connection connection;
    private PreparedStatement selectAllResearchers;
    private PreparedStatement selResForScrape;
    private PreparedStatement selPubIdByTitle;
    private PreparedStatement insResearcher;
    private PreparedStatement insTitle;
    private PreparedStatement updResearcher;
    private PreparedStatement delResearcher;
    private PreparedStatement insPubRes;
    private PreparedStatement insCitations;
    private PreparedStatement selAllPubRes;
    private PreparedStatement selCitation;
    private PreparedStatement updCitations;
    private PreparedStatement updLastUpdate;
    private PreparedStatement selResearher;
    private PreparedStatement selPubCitByRes;
    
    public PostgresDBClient() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            
            // prepare queries, query-names match those of respective method
            selectAllResearchers = connection.prepareStatement("SELECT researcher_id, name_gr, surname_gr,"
                    + " name, surname, email, last_update FROM public.researcher",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            selResearher = connection.prepareStatement("SELECT researcher_id, name_gr, surname_gr FROM researcher ORDER BY surname_gr");
            selResForScrape = connection.prepareStatement("SELECT name, surname FROM researcher "
                    + "WHERE researcher_id = ?");
            selPubIdByTitle = connection.prepareStatement("SELECT publication_id FROM publication"
                    + " WHERE title = ?");
            selPubCitByRes = connection.prepareStatement("SELECT publication.title, citations.\"number\", " +
                    "citations.origin, researcher.last_update FROM public.citations, public.pub_res, "
                    + "public.publication, public.researcher WHERE publication.publication_id = pub_res.publication_id"
                    + " AND publication.publication_id = citations.publication_id AND researcher.researcher_id = pub_res.researcher_id"
                    + " AND researcher.researcher_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            insResearcher = connection.prepareStatement("INSERT INTO public.researcher "
                    + "(name_gr, surname_gr, name, surname, email) VALUES ( ?, ?, ?, ?, ?)");
            insTitle = connection.prepareStatement("INSERT INTO publication (title) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            updResearcher = connection.prepareStatement("UPDATE public.researcher SET name_gr = ?,"
                    + " surname_gr = ?, name = ?, surname = ?, email = ? WHERE researcher_id = ?");
            updCitations = connection.prepareStatement("UPDATE citations SET number = ? WHERE origin = ? AND publication_id = ?;");
            updLastUpdate = connection.prepareStatement("UPDATE researcher SET last_update = CURRENT_DATE WHERE researcher_id = ?");
            delResearcher = connection.prepareStatement("DELETE from public.researcher WHERE researcher_id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            insPubRes = connection.prepareStatement("INSERT INTO pub_res (publication_id, researcher_id) VALUES (?, ?);");
            insCitations = connection.prepareStatement("INSERT INTO citations (origin, publication_id, number)"
                    + " VALUES (?, ?, ?)");
            selAllPubRes = connection.prepareStatement("SELECT * FROM pub_res WHERE researcher_id = ? AND publication_id = ?");
            selCitation = connection.prepareStatement("SELECT number FROM citations WHERE origin = ? AND publication_id = ?");
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // select all records from researcher table to show in main window
    public ResultSet selectAllResearchers() {
        
        try {
            return selectAllResearchers.executeQuery();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    // select researcher attributes for combobox
    public ArrayList<Researcher> selResearher() {
        ArrayList<Researcher> result = new ArrayList<Researcher>();
        try {
            ResultSet resultSet = selResearher.executeQuery();
            while (resultSet.next()) {
                result.add(new Researcher(resultSet.getInt(1),resultSet.getString(2), resultSet.getString(3)));
            }
        }
        catch (SQLException sqlException) {
         sqlException.printStackTrace();
        }
        
        if (result.isEmpty())
            result.add(new Researcher(-1, "Όνομα", "Επώνυμο"));
        return result;
    }
    // select researcher's necessary scrape attributes
    public String[] selResForScrape(int researcher_id) {
        String[] researcherFullName = {"",""};
        try
        {
            selResForScrape.setInt(1, researcher_id);
            System.out.println(selResForScrape.toString());
            ResultSet resultSet =  selResForScrape.executeQuery();
            if (resultSet.next()) {
                researcherFullName[0] = resultSet.getString(1);
                researcherFullName[1] = resultSet.getString(2);
            }
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();            
        }
        return researcherFullName;
    }
    
    // select all records from pub_res to check if pair researcher-publication exists
    public boolean selAllPubRes(int researcher_id, int publication_id) {
        boolean response = false;
        try {
            selAllPubRes.setInt(1, researcher_id);
            selAllPubRes.setInt(2, publication_id);
            
            response = selAllPubRes.execute();
        }
        catch(SQLException sqlException) {
            
        }
        return response;
    }
            
    // select a publication by title
    public int selPubIdByTitle(String title) {
        int response = -1;
        
        try 
        {
            selPubIdByTitle.setString(1, title);
            ResultSet resultSet = selPubIdByTitle.executeQuery();
            if (resultSet.next())
                response = resultSet.getInt(1);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return response;
    }
    
    // select a citation
    public int selCitation(String origin, int publication_id) {
        int response = -1;
        try {
            selCitation.setString(1, origin);
            selCitation.setInt(2, publication_id);
            
            ResultSet resultSet = selCitation.executeQuery();
            if (resultSet.next())
                response = resultSet.getInt(1);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return response;
    }
    
    // select publications and citations by researcher
    public ResultSet selPubCitByRes(int researcher_id) {
        try {
            selPubCitByRes.setInt(1, researcher_id);
            return selPubCitByRes.executeQuery();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    
    // insert new record in table researcher
    public void insResearcher(String nameGr, String surNameGr, String name, String surName, String email) {
        try 
        {
            insResearcher.setString(1, nameGr);
            insResearcher.setString(2, surNameGr);
            insResearcher.setString(3, name);
            insResearcher.setString(4, surName);
            insResearcher.setString(5, email);
            
            insResearcher.executeUpdate();
            
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // insert new record in table pub_res
    public void insPubRes(int publication_id , int researcher_id) {
        try {
            insPubRes.setInt(1, publication_id);
            insPubRes.setInt(2, researcher_id);
            
            insPubRes.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // insert a new title in publication
    public int insTitle(String title) {
        int response = -1;
        try
        {
            insTitle.setString(1, title);
            insTitle.executeUpdate();
            ResultSet resultSet = insTitle.getGeneratedKeys();
            if (resultSet.next())
                response = resultSet.getInt(1);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return response;
    }
    
    // insert a citation in table citations
    public void insCitations(String origin, int publication_id, int number) {
        try {
            insCitations.setString(1, origin);                    
            insCitations.setInt(2, publication_id);
            insCitations.setInt(3, number);
            
            insCitations.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    public void updLastUpdate(int researcher_id) {
        try {
            updLastUpdate.setInt(1, researcher_id);
            updLastUpdate.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // update record in researcher table
    public void updResearcher(int researcher_id, String nameGr, String surNameGr, String name, String surName, String email) {
        try
        {
            updResearcher.setString(1, nameGr);
            updResearcher.setString(2, surNameGr);
            updResearcher.setString(3, name);
            updResearcher.setString(4, surName);
            updResearcher.setString(5, email);
            updResearcher.setInt(6, researcher_id);
            
            updResearcher.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // update number in citations table
    public void updCitations(int number, String origin, int publication_id) {
        try {
            updCitations.setInt(1, number);
            updCitations.setString(2, origin);
            updCitations.setInt(3, publication_id);
            
            updCitations.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    // delete record from researcher table
    public void delResearcher(int researcher_id) {
        try
        {
            delResearcher.setInt(1, researcher_id);
            delResearcher.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // close connection to release database resources
    public void conClose() {
        try {
            connection.close();
        }
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }        
    }
}
