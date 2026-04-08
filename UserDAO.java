package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointments;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** This is an abstract class that creates the Appointments data access object.
 * This class extends the JDBC class. */
public abstract class AppointmentDAO extends JDBC
{
    /** This is a static method.
     * This method will insert data from the application to the database.
     * @param title Title Column String
     * @param description Description Column String
     * @param location Location Column String
     * @param type Type Column String
     * @param start Start Column LocalDateTime
     * @param end End Column LocalDateTime
     * @param createdBy Created_By Column String
     * @param lastUpdateBy Last_Update_By Column String
     * @param customerID Customer_ID Column Long
     * @param userID User_ID Column Long
     * @param contactID Contact_ID Column Long
     * @throws SQLException SQL method
     * @return rowsAffected */
    public static int insert(String title,
                              String description,
                              String location,
                              String type,
                              LocalDateTime start,
                              LocalDateTime end,
                              String createdBy,
                              String lastUpdateBy,
                              long customerID,
                              long userID,
                              long contactID) throws SQLException
    {
        String sql = "INSERT INTO APPOINTMENTS (title, description, location, type, start, end, " +
                "create_date, created_by, last_update, last_updated_by, customer_id, user_id, contact_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4,type);
        ps.setTimestamp(5, Timestamp.valueOf(start));
        ps.setTimestamp(6, Timestamp.valueOf(end));
        ps.setString(7, createdBy);
        ps.setString(8, lastUpdateBy);
        ps.setLong(9, customerID);
        ps.setLong(10, userID);
        ps.setLong(11, contactID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This is a static method.
     * This method will update data from the application to the database.
     * @param appointmentID Appointment ID Integer
     * @param title Title Column String
     * @param description Description Column String
     * @param location Location Column String
     * @param type Type Column String
     * @param start Start Column LocalDateTime
     * @param end End Column LocalDateTime
     * @param lastUpdateBy Last_Update_By Column String
     * @param customerID Customer_ID Column Long
     * @param userID User_ID Column Long
     * @param contactID Contact_ID Column Long
     * @throws SQLException SQL method
     * @return rowsAffected */
    public static int update(long appointmentID,
                             String title,
                             String description,
                             String location,
                             String type,
                             LocalDateTime start,
                             LocalDateTime end,
                             String lastUpdateBy,
                             long customerID,
                             long userID,
                             long contactID) throws SQLException
    {
        String sql = "UPDATE APPOINTMENTS SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, " +
                "Last_Update=NOW(), Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, Timestamp.valueOf(start));
        ps.setTimestamp(6, Timestamp.valueOf(end));
        ps.setString(7, lastUpdateBy);
        ps.setLong(8, customerID);
        ps.setLong(9, userID);
        ps.setLong(10, contactID);
        ps.setLong(11, appointmentID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This is a static method.
     * This method will delete appointment records form the database.
     * @param appointmentID Appointment_ID Column Integer
     * @throws SQLException SQL Method
     * @return rowsAffected */
    public static int delete(int appointmentID) throws SQLException
    {
        String sql = "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    /** This is a static method.
     * This method will retrieve count from data in the database.
     * @param type Type Column String
     * @param month Month from Start Integer
     * @throws SQLException SQL method
     * @return total Total of Count */
    public static int countMonth(String type, int month) throws SQLException
    {
        String sql = "SELECT count(*) AS total FROM appointments WHERE Type = ? AND MONTH(Start) = ? AND YEAR(Start) = YEAR(NOW())";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, type);
        ps.setInt(2, month);
        int total = 0;
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            total = rs.getInt("total");
        }
        return total;
    }

    /** This is a static method Observable List.
     *  This method will select all data from database that is specified.
     *  @throws SQLException SQL Method
     *  @returns allAppointments */
    public static ObservableList<Appointments> selectAll() throws SQLException
    {
        ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();

        String sql = "SELECT * FROM APPOINTMENTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdateBy = rs.getString("Last_Updated_By");
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            Appointments appointment = new Appointments (appointmentID, title, description, location, type, start, end, createdDate, createdBy, lastUpdate, lastUpdateBy, customerID, userID, contactID);
            allAppointments.add(appointment);
        }
        return allAppointments;
    }

    /** This is a static method.
     * This is a select method that will retrive data from an record with matching parameter.
     * @param appointmentID Appointment_ID Column Integer
     * @throws SQLException SQL Method
     * @return null */
    public static Appointments select(int appointmentID) throws SQLException
    {
        String sql = "SELECT * FROM APPOINTMENTS WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);
        ResultSet rs = ps.executeQuery();
        if(rs.next())
        {
            int appointmentIDPK = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            LocalDateTime createdDate = rs.getTimestamp("Create_Date").toLocalDateTime();
            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdateBy = rs.getString("Last_Updated_By");
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            Appointments appointment = new Appointments (appointmentID, title, description, location, type, start, end, createdDate, createdBy, lastUpdate, lastUpdateBy, customerID, userID, contactID);
            return appointment;
        }
        return null;
    }

    /** This is a static method.
     * This is a select method that will retrive data from an record with matching parameter.
     * @throws SQLException SQL Method
     * @return typeList A distinct list of types */
    public static ObservableList<Appointments> selectType() throws SQLException{
        ObservableList<Appointments> typeList = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT Type FROM APPOINTMENTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            String type = rs.getString("Type");
            Appointments apptType = new Appointments(type);
            typeList.add(apptType);
        }
        return typeList;
    }

}