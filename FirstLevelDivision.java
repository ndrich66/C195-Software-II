package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This is an abstract class that creates the First Level Division data access object.
 * This class ectends the abstract class JDBC. */
public abstract class FirstLevelDivisionDao extends JDBC
{
    /** This is a static Observable List method.
     * This method retrieves all records from the First_Level_Divisions table in the database.
     * @throws SQLException SQL Method
     * @return allDivisions */
    public static ObservableList<FirstLevelDivision> selectAll() throws SQLException
    {
        ObservableList<FirstLevelDivision> allDivisions = FXCollections.observableArrayList();

        String sql = "SELECT * FROM FIRST_LEVEL_DIVISIONS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String division = rs.getString("Division");
            int countryID = rs.getInt("Country_ID");

            FirstLevelDivision fld = new FirstLevelDivision(divisionID, division, countryID);
            allDivisions.add(fld);
        }
        return allDivisions;
    }

    /** This is a static Observable List method.
     * This method retrieves all data for an associated record.
     * @param countryID Country_ID Column long
     * @throws SQLException SQL Method
     * @return filteredDivisions */
    public static ObservableList<FirstLevelDivision> selectDivisions(long countryID) throws SQLException
    {
        ObservableList<FirstLevelDivision> filteredDivisions = FXCollections.observableArrayList();

        String sql = "SELECT * FROM FIRST_LEVEL_DIVISIONS WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setLong(1, countryID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int divisionID = rs.getInt("Division_ID");
            String divisions = rs.getString("Division");

            FirstLevelDivision fldf = new FirstLevelDivision(divisions, divisionID);
            filteredDivisions.add(fldf);
        }
        return filteredDivisions;
    }
}