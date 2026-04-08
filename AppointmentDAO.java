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
import model.FirstLevelDivision;
import dao.CountriesDAO;
import dao.CustomerDAO;
import dao.FirstLevelDivisionDao;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** This is a class the creates a screen where the user can add a new customer object.
 * This method implements Initializable. */
public class AddCustomerController implements Initializable
{
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

    /** This is an on action method.
     * This method will insert data into the database.
     * This method calls an insert method from the CustomerDao to insert new record into the database.
     * The user will be taken back to the main screen after success.
     * There is a couple alerts for user if there are incorrect values entered or null.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionInsert(ActionEvent actionEvent) throws IOException
    {
        try
        {
            String customerName = custNameTxt.getText();
            String address = addressTxt.getText();
            String phone = phoneTxt.getText();
            String postalCode = postalCodeTxt.getText();
            String createdBy = "script";
            String lastUpdateBy = "script";
            long divisionID = divisionComBx.getValue().getDivisionID();

            if(customerName.isBlank() || address.isBlank() || postalCode.isBlank() || phone.isBlank())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid values for each field.");
                alert.setTitle("Error Adding Customer");
                alert.showAndWait();
            }
            else
            {

                long rowsAffected = CustomerDAO.insert(customerName, address, postalCode, phone, createdBy, lastUpdateBy, (int) divisionID);
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
            alert.setTitle("Error Adding Customer");
            alert.showAndWait();
        }
    }

    /** This is an on action method.
     * This method cancels adding a customer object and takes the user back to the appointment main screen.
     * There is an alert to the user verifying canceling adding new customer object.
     * There is a lambda done on the alert to make the program run more efficently.
     * This method no longer throws IOException
     * @param actionEvent Button Click */
    public void onActionCancel(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Cancel Confirmation");
        alert.setContentText("Are you sure you would like to cancel adding a new customer?");

        //lambda for alert here
        alert.showAndWait().ifPresent((response -> {
            if (response == ButtonType.OK) {
                System.out.println("Alerting!");
                try {
                    stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }));
    }

    /** This is an on action method.
     * This method gets called when there is a selection made on the country combo box.
     * There is a method called from the FirstLevelDivisionDAO to set the combo box.
     * @param actionEvent Combo Box Selection
     * @throws SQLException SQL called */
    public void onActionCountryCmbBx(ActionEvent actionEvent) throws SQLException {
        Countries country = countryComBx.getSelectionModel().getSelectedItem();
        long countryID = country.getCountryID();
        divisionComBx.setItems(FirstLevelDivisionDao.selectDivisions(countryID));
    }

    /** This in an override method.
     * This is the first method to get called for this screen.
     * This method sets the data objects for the combo box.
     * There is a method called from the CountriesDAO to set the combo box.
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