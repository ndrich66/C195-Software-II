package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Countries;
import model.Customers;
import model.FirstLevelDivision;
import dao.CountriesDAO;
import dao.CustomerDAO;
import dao.FirstLevelDivisionDao;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/** This is a class the creates the screen to edit customer data.
 * This method implements Initializable. */
public class EditCustomerController implements Initializable
{
    @FXML
    private TextField custIDTxt;
    @FXML
    private TextField custNameTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private TextField phoneTxt;
    @FXML
    private TextField postalCodeTxt;
    @FXML
    private ComboBox<Countries> countryComBx;
    @FXML
    private ComboBox<FirstLevelDivision> divisionComBx;

    Stage stage;
    Parent scene;

    /** This is a method that sends data to the correct screen.
     * This method gets called on the appointments main screen.
     * There is a method called from the FirstLevelDivisionDao to set a combo box.
     * @param customers Customer Object
     * @throws SQLException SQL called */
    public void sendCust(Customers customers) throws SQLException
    {
        custIDTxt.setText(String.valueOf(customers.getCustomerID()));
        custNameTxt.setText(customers.getCustomerName());
        addressTxt.setText(customers.getAddress());
        phoneTxt.setText(customers.getPhone());
        postalCodeTxt.setText(customers.getPostalCode());

        Countries country = null;
        for (int i = 0; i < countryComBx.getItems().size(); i++)
        {
            country = countryComBx.getItems().get(i);
            if (customers.getCountryID() == country.getCountryID())
            {
                countryComBx.setValue(country);
                break;
            }
        }

        divisionComBx.setItems(FirstLevelDivisionDao.selectDivisions(country.getCountryID()));
        for(FirstLevelDivision fld : divisionComBx.getItems())
        {
            if (customers.getDivisionID() == fld.getDivisionID())
            {
                divisionComBx.setValue(fld);
            }
        }
    }

    /** This method calls an update method from the CustomerDAO.
     * There is a method called from the CustomerDAO to update record in the database.
     * This will take the user back to the application main screen after success.
     *  There are a couple alerts to the user for valid data entry.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionUpdate(ActionEvent actionEvent) throws IOException
    {
        try {
            int customerID = Integer.parseInt(custIDTxt.getText());
            String customerName = custNameTxt.getText();
            String address = addressTxt.getText();
            String postalCode = postalCodeTxt.getText();
            String phone = phoneTxt.getText();
            String lastUpdatedBy = "script";
            int divisionID = (int) divisionComBx.getValue().getDivisionID();

            if(customerName.isBlank() || address.isBlank() || postalCode.isBlank() || phone.isBlank())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid values for each field.");
                alert.setTitle("Error Editing Customer");
                alert.showAndWait();
            }
            else
            {
                int rowsAffected = CustomerDAO.update(customerID, customerName, address, phone, postalCode, lastUpdatedBy, divisionID);
                System.out.println(rowsAffected);

                stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        }
        catch (NullPointerException | SQLException n)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid values for each field.");
            alert.setTitle("Error Editing Customer");
            alert.showAndWait();
        }
    }

    /** This is an on action method.
     * This method allows the user to cancel editing the customer object.
     * The user will be taken back to the main appointment screen on confirmation of alert.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionCancel(ActionEvent actionEvent) throws IOException
    {
        Alert alert  = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to cancel editing this customer?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK)
        {
            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /** This is an on action method.
     * This method gets called when there is a selection made on the country combo box.
     * There is a method called from the FirstLevelDivisionDAO to set the combo box.
     * @param actionEvent Combo Box Selection
     * @throws SQLException SQL called */
    public void onActionCountryCmbBx(ActionEvent actionEvent) throws SQLException
    {
        Countries country = countryComBx.getSelectionModel().getSelectedItem();
        long countryID = country.getCountryID();
        divisionComBx.setItems(FirstLevelDivisionDao.selectDivisions(countryID));
    }

    /** This in an override method.
     * This is the first method to get called for this screen.
     * This method sets the data objects for the combo box.
     * There is a method called from the CountriesDAO to set data for the combo box.
     * @param resourceBundle for local objects.
     * @param url for global objects. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try
        {
            countryComBx.setItems(CountriesDAO.selectAll());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}