package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customers;
import model.User;
import sample.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** This is an abstract class that creates the Customer data access object.
 * This class extends the JDBC class. */
public abstract class CustomerDAO extends JDBC
{
    /** This is a static method.
     * This method will insert data from the application into the database as a new record.
     * @param customerName Customer_Name Column String
     * @param address Address Column String
     * @param postalCode Postal_Code Column String
     * @param phone Phone Column String
     * @param createdBy Created_By String
     * @param lastUpdateBy Last_Updated_By Column String
     * @param divisionID Division_ID Column Integer
     * @throws SQLException SQL Method
     * @return rowsAffected */
    public static long insert(String customerName,
                              String address,
                              String postalCode,
                              String phone,
                              String createdBy,
                              String lastUpdateBy,
                              int divisionID) throws SQLException
    {
        String sql = "INSERT INTO CUSTOMERS (customer_name, address, postal_code, phone, " +
                "create_date, created_by, last_update, last_updated_by, division_id) " +
                "VALUES (?, ?, ?, ?, NOW(), ?, NOW(), ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postalCode);
        ps.setString(4, phone);
        ps.setString(5, createdBy);
        ps.setString(6, lastUpdateBy);
        ps.setInt(7, divisionID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This is a static method.
     * This method will update a record from the application into the database.
     * @param customerID Integer
     * @param customerName String
     * @param address String
     * @param postalCode String
     * @param phone String
     * @param lastUpdateBy String
     * @param divisionID Integer
     * @throws SQLException SQL Method
     * @return rowsAffected */
    public static int update(int customerID,
                             String customerName,
                             String address,
                             String postalCode,
                             String phone,
                             String lastUpdateBy,
                             int divisionID) throws SQLException
    {
        String sql = "UPDATE CUSTOMERS SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, " +
                "Last_Update=NOW(), Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postalCode);
        ps.setString(4, phone);
        ps.setString(5, lastUpdateBy);
        ps.setInt(6, divisionID);
        ps.setInt(7, customerID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This is a static method for deletion.
     * This method will delete a customer record from the selected parameter.
     * @param customerID Customer_ID Column Integer
     * @throws SQLException SQL Method
     * @return rowsAffected */
    public static long delete(int customerID) throws SQLException
    {
        String sql = "DELETE FROM CUSTOMERS WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This is a static ObservableList method.
     * This method will select all records from the Customrs table in the database.
     * @throws SQLException SQL Method
     * @return allCustomers */
    public static ObservableList <Customers> selectAll() throws SQLException
    {
        ObservableList<Customers> allCustomers = FXCollections.observableArrayList();

        String sql = "SELECT * FROM CUSTOMERS INNER JOIN FIRST_LEVEL_DIVISIONS ON CUSTOMERS.Division_ID = FIRST_LEVEL_DIVISIONS.Division_ID " +
                "INNER JOIN COUNTRIES ON FIRST_LEVEL_DIVISIONS.Country_ID = COUNTRIES.Country_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String postalCode = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdateBy = rs.getString("Last_Updated_By");
            int divisionID = rs.getInt("Division_ID");
            int countryID = rs.getInt("Country_ID");

            Customers customer = new Customers (customerID, customerName, address, postalCode, phone, createdDate, createdBy, lastUpdate, lastUpdateBy, divisionID, countryID);
            allCustomers.add(customer);
        }
        return allCustomers;
    }
}