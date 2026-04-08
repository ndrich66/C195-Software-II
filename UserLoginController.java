package controllers;

import dao.AppointmentDAO;
import dao.ContactsDAO;
import dao.CustomerDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

/** This is a class that creates a screen where the user can add appointments.
 * This method implements Initializable. */
public class AddAppointmentController implements Initializable
{
    @FXML
    private TextField titleTxt;
    @FXML
    private TextField descriptionTxt;
    @FXML
    private TextField locationTxtField;
    @FXML
    private TextField typeTxt;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<LocalTime> startTimeCmbBx;

    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<LocalTime> endTimeCmbBx;

    @FXML
    private ComboBox<Customers> customerIDComboBx;
    @FXML
    private ComboBox<User> userIDComboBx;
    @FXML
    private ComboBox<Contacts> contactIDComboBx;

    Stage stage;
    Parent scene;

    /** This is an on action method.
     * This method will take the user back to the main screen without saving data.
     * There is an alert to the user to verify the user wants to return without saving.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionCancel(ActionEvent actionEvent) throws IOException
    {
        Alert alert  = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to cancel adding a new appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK)
        {
            stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /** This is an on action method for inserting data.
     * This method calls an insert method from the AppointmentDAO.
     * This will take the user back to the application main screen after success.
     * There are several alerts to the user to enter valid data and operational business  hours.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionInsert(ActionEvent actionEvent) throws IOException
    {
        boolean overlapFlag = false;
        try {
            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxtField.getText();
            String type = typeTxt.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalTime startTime = startTimeCmbBx.getValue();
            LocalDate endDate = endDatePicker.getValue();
            LocalTime endTime = endTimeCmbBx.getValue();
            LocalDateTime start = LocalDateTime.of(startDate, startTime);
            LocalDateTime end = LocalDateTime.of(endDate, endTime);
            String createdBy = "script";
            String lastUpdateBy = "script";
            long customerID = customerIDComboBx.getValue().getCustomerID();
            long userID = userIDComboBx.getValue().getUserID();
            long contactID = contactIDComboBx.getValue().getContactID();

            ZoneId myZoneId = ZoneId.systemDefault();

            ZonedDateTime myStartZDT = ZonedDateTime.of(start, myZoneId);
            ZonedDateTime myEndZDT = ZonedDateTime.of(end, myZoneId);

            ZoneId estZoneID = ZoneId.of("America/New_York");

            ZonedDateTime estStartZDT = ZonedDateTime.ofInstant(myStartZDT.toInstant(), estZoneID);
            ZonedDateTime estEndZTD = ZonedDateTime.ofInstant(myEndZDT.toInstant(), estZoneID);

            LocalTime businessStart = LocalTime.of(8, 0);
            LocalTime businessEnd = LocalTime.of(22, 0);

            if(estStartZDT.toLocalTime().isBefore(businessStart) ||
                    estEndZTD.toLocalTime().isAfter(businessEnd))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Business hours are 0800 to 2200 EST.");
                alert.setTitle("Error Adding Appointment");
                alert.showAndWait();
            }
            else if (start.isBefore(LocalDateTime.now()) ||
                    start.isEqual(end) || end.isBefore(start))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Pick valid dates and times.");
                alert.setTitle("Error Adding Appointment");
                alert.showAndWait();
            }
            else if(title.isBlank() || description.isBlank() || location.isBlank() || type.isBlank())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid entries for each field.");
                alert.setTitle("Error Adding Appointment");
                alert.showAndWait();
            }
            else
            {
                for (Appointments appointments : AppointmentDAO.selectAll())
                {
                    LocalDateTime checkingStart = appointments.getStart();
                    LocalDateTime checkingEnd = appointments.getEnd();

                    if ((customerID == appointments.getCustomerID()) &&
                            (start.isBefore(checkingStart) && end.isAfter(checkingEnd)) ||
                            ((start.isAfter(checkingStart)) && (start.isBefore(checkingEnd))) ||
                            (end.isAfter(checkingStart)) && (end.isBefore(checkingEnd)) ||
                            (start.isEqual(checkingStart) || (end.isEqual(checkingEnd))))
                    {
                        overlapFlag = true;

                        Alert alert = new Alert(Alert.AlertType.ERROR, "There is an overlap of appointments.");
                        alert.setTitle("Error Adding Appointment");
                        alert.showAndWait();
                        break;
                    }
                }
                if(!overlapFlag)
                {

                    long rowsAffected = AppointmentDAO.insert(title, description, location, type, start, end, createdBy, lastUpdateBy, customerID, userID, contactID);
                    System.out.println(rowsAffected);

                    stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                }
            }
        }
        catch (NullPointerException | SQLException n)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid values for each field.");
            alert.setTitle("Error Adding Appointment");
            alert.showAndWait();
        }
    }

    /** This is an override method.
     * This is the first method to get called on this screen.
     * There are several methods called from various DAO classes to set combo boxes.
     * @param resourceBundle for local dates and times.
     * @param url for global dates and times. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        for(int i = 0; i < 24; i++)
        {
            startTimeCmbBx.getItems().add(LocalTime.of(i, 0));
            if (i < 23) {
                endTimeCmbBx.getItems().add(LocalTime.of(i + 1, 0));
            } else {
                endTimeCmbBx.getItems().add(LocalTime.of(0, 0));
            }
        }

        try
        {
            customerIDComboBx.setItems(CustomerDAO.selectAll());
            userIDComboBx.setItems(UserDAO.selectAll());
            contactIDComboBx.setItems(ContactsDAO.selectAll());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}