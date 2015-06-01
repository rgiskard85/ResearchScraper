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
import java.sql.Statement;

/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */
public class PostgresDBClient {
    final String dbUrl = "jdbc:postgresql://83.212.122.8:5432/postgres";
    final String dbUser = "akademia";
    final String dbPass = "ds525";
    
    public PostgresDBClient() {        
    }
    
    // insert new record in table researcher
    public void insResearcher(String nameGr, String surNameGr, String name, String surName, String email) {
        String query = "INSERT INTO public.researcher (name_gr, surname_gr, name, surname, email) ";
        query = query + "VALUES ('" + nameGr + "','" + surNameGr + "','" + name + "','" + surNameGr + "','" + email + "')";
        try (                
                Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
                Statement statement = connection.createStatement();
                )
        {
            statement.executeUpdate(query);
            
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // select all records from researcher table to show in main window
    public ResultSet selectAllResearchers() {
        String query = "SELECT researcher_id, name_gr, surname_gr, name, surname, email, last_update FROM public.researcher";
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return statement.executeQuery(query);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    
    // update record in researcher table
    public void updResearcher(String researcher_id, String nameGr, String surNameGr, String name, String surName, String email) {
        String query = "UPDATE public.researcher SET name_gr = '" + nameGr + "', surname_gr = '" + surNameGr + "', ";
        query = query + "name = '" + name + "', surname = '" + surName +"', email = '" + email + "'";
        query = query + "where researcher_id = " + researcher_id;
        
        try (
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            Statement statement = connection.createStatement()
                )
        {
            statement.executeUpdate(query);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    
    // delete record from researcher table
    public void delResearcher(String researcher_id) {
        String query = "DELETE from public.researcher WHERE researcher_id = " + researcher_id;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
                Statement statement = connection.createStatement())
        {
            statement.executeUpdate(query);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
