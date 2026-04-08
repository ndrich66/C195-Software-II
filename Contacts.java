package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Countries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** This is an abstract class that creates the Countries data access object.
 * This class extends the JDBC abstract class. */
public abstract class CountriesDAO extends JDBC {

    /** This is a static Observable List method.
     * This method will retrieve all records from the associated table.
     * @throws SQLException SQL Method
     * @returns allCountries */
    public static ObservableList<Countries> selectAll() throws SQLException
    {
        ObservableList<Countries> allCountries = FXCollections.observableArrayList();

        String sql = "SELECT * FROM COUNTRIES";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            int countryID = rs.getInt("Country_ID");
            String country = rs.getString("Country");

            Countries countries = new Countries (countryID, country);
            allCountries.add(countries);
        }
        return allCountries;
    }
}
