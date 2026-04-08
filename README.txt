package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This is an abstract class that creates the User data access object.
 * This class extends the abstract class JDBC. */
public abstract class UserDAO extends JDBC
{
    /** This is a static Observable List method.
     * This method retrieves all records for the Users.
     * @throws SQLException SQL Method
     * @return allUsers */
    public static ObservableList<User> selectAll() throws SQLException
    {
        ObservableList<User> allUsers = FXCollections.observableArrayList();

        String sql = "SELECT * FROM USERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String password = rs.getString("Password");

            User user = new User (userID, userName, password);
            allUsers.add(user);
        }
        return allUsers;
    }
}

