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
    private PreparedStatement selResFromPub;
    
    public PostgresDBClient() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            
            // prepare queries, query-names match those of respective method
            selectAllResearchers = connection.prepareStatement("SELECT researcher_id, name_gr, surname_gr,"
                    + " name, surname, email, last_update FROM public.researcher");
            selResForScrape = connection.prepareStatement("SELECT name, surname FROM researcher "
                    + "WHERE researcher_id = ?");
            selPubIdByTitle = connection.prepareStatement("SELECT publication_id FROM publication"
                    + " WHERE title = ?");
            insResearcher = connection.prepareStatement("INSERT INTO public.researcher "
                    + "(name_gr, surname_gr, name, surname, email) VALUES ( ?, ?, ?, ?, ?)");
            insTitle = connection.prepareStatement("INSERT INTO publication (title) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            updResearcher = connection.prepareStatement("UPDATE public.researcher SET name_gr = ?,"
                    + " surname_gr = ?, name = ?, surname = ?, email = ? WHERE researcher_id = ?");
            delResearcher = connection.prepareStatement("DELETE from public.researcher WHERE researcher_id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            insPubRes = connection.prepareStatement("INSERT INTO pub_res (publication_id, researcher_id) VALUES (?, ?);");
            insCitations = connection.prepareStatement("INSERT INTO citations (origin, publication_id, number)"
                    + " VALUES ('scholar', ?, ?)");
            selResFromPub = connection.prepareStatement(dbUrl);
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
    
    // select researcher's necessary scrape attributes
    public String[] selResForScrape(String researcher_id) {
        String[] researcherFullName = {"",""};
        try
        {
            selResForScrape.setString(1, researcher_id);
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
    public boolean selRes
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
    public void insPubRes(int publication_id , String researcher_id) {
        try {
            insPubRes.setInt(1, publication_id);
            insPubRes.setString(2, researcher_id);
            
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
    public void insCitations(int publication_id, String number) {
        try {
            insCitations.setInt(1, publication_id);
            insCitations.setString(2, number);
            
            insCitations.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    // update record in researcher table
    public void updResearcher(String researcher_id, String nameGr, String surNameGr, String name, String surName, String email) {
        try
        {
            updResearcher.setString(1, nameGr);
            updResearcher.setString(2, surNameGr);
            updResearcher.setString(3, name);
            updResearcher.setString(4, surName);
            updResearcher.setString(5, email);
            updResearcher.setString(6, researcher_id);
            
            updResearcher.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // delete record from researcher table
    public void delResearcher(String researcher_id) {
        try
        {
            delResearcher.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
