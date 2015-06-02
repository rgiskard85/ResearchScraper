/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */
public class Test {
    public static void main(String args[]) throws SQLException {
        String dbUrl = "jdbc:postgresql://83.212.122.8:5432/postgres";
        String dbUser = "akademia";
        String dbPass = "ds525";
        Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        
        PreparedStatement insert = connection.prepareStatement("INSERT INTO publication (title) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS);
        insert.setString(1, "some publication");
        
        insert.executeUpdate();
        ResultSet resultSet = insert.getGeneratedKeys();
        
        if(resultSet.next())
            System.out.println(resultSet.getInt(1));
        
        resultSet.close();
        insert.close();
        connection.close();
    }
}
