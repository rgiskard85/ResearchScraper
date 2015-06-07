
package database;

/**
 *
 * @author P. & V. Deitel
 *  Modified by Dionysios-Charalampos Vythoulkas <dcvythoulkas@gmail.com>
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;


public class ResultSetTableModelPub extends AbstractTableModel {
    private final PostgresDBClient pDBC;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;
    private int researcher_id;
    
    // keep track of database connection status
    private boolean connectedToDatabase = false;
    
    // constuctor initializes resultSet and obtains its metadata object;
    // determines number of rows
    public ResultSetTableModelPub(int researcher_id) throws SQLException {
        pDBC = new PostgresDBClient();
        // update database connection status
        connectedToDatabase = true;
        
        // set query and execute it
        updateView(researcher_id);
    }
    
    // get class that represents column type
    public Class getColumnClass(int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        
        // determine Java class of column
        try {
            String className = metaData.getColumnClassName(column + 1);
            
            // return Class object that represents className
            return Class.forName(className);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return Object.class; // if problems occur above, assume type object
    }
    
    // get number of columns in ResultSet
    public int getColumnCount() throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        
        // determine number of columns
        try {
            return metaData.getColumnCount();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        
        return 0; // if problems occur above, return 0 for number of columns
    }
    
    // get name of a particular column in ResultSet
    public String getColumnName(int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
            
        // determine column name
        try {
            return metaData.getColumnName(column + 1);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return ""; // if problem, return empty string for column name
    }
    
    // return number of rows in ResultSet
    public int getRowCount() throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        return numberOfRows;
    }
    
    // obtain value in particular row and column
    public Object getValueAt(int row, int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase)
            throw new IllegalStateException("Not connected to databse");
        // obtain a value at spcecified ResultSet row and column
        try {
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        
        return ""; // if problems, return empty string object        
    }
    
    public final void updateView(int researcher_id) throws SQLException {
        this.researcher_id = researcher_id;
        resultSet = pDBC.selectAllResearchers();
        resultSet = pDBC.selPubCitByRes(researcher_id);
        
        // obtain metadata for ResultSet
        metaData = resultSet.getMetaData();
        
        // determine number of rows in ResultSet
        resultSet.last(); // move to last row
        numberOfRows = resultSet.getRow(); // get row number
        
        // notify JTable that model has changed
        fireTableStructureChanged();
    }
    
    // close Statement and Connection
    public void disconnectFromDatabase() {
        if (connectedToDatabase) {
            // close Statement and Connection
            try {
                resultSet.close();
            }
            catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            finally { // update database connection status
                connectedToDatabase = false;
            }
        }
    }
} // end class ResultSetTableModel
