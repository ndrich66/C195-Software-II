package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contacts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This is an abstract class that creates the Contacts data access object.
 * This class extends the JDBC abstract Class. */
public abstract class ContactsDAO extends JDBC {

    /** This is a static Observable List method.
     * This method will return all data available for contacts.
     * @throws SQLException SQL Method
     * @return allContacts */
    public static ObservableList<Contacts> selectAll() throws SQLException
    {
        ObservableList<Contacts> allContacts = FXCollections.observableArrayList();

        String sql = "SELECT * FROM CONTACTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String email = rs.getString("Email");

            Contacts contacts = new Contacts (contactID, contactName, email);
            allContacts.add(contacts);
        }
        return allContacts;
    }

    /** This is a static method.
     * This method selects a record with a corresponding parameter.
     * @param contactID Contact_ID Column Integer
     * @throws SQLException SQL Method */
    public static void select(int contactID) throws SQLException
    {
        String sql = "SELECT * FROM CONTACTS WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactID);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            int contactId = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String email = rs.getString("Email");
        }
    }
}
