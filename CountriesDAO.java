package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointments;
import model.Contacts;
import model.Customers;
import model.User;
import dao.AppointmentDAO;
import dao.ContactsDAO;
import dao.CustomerDAO;
import dao.UserDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

/** This class creates a screen that allows the user to edit a appointment object.
 * This class implements Initializable. */
public class EditAppointmentController implements Initializable
{
    @FXML
    private TextField apptIDTxt;
    @FXML
    private TextField titleTxt;
    @FXML
    private TextField descriptionTxt;
    @FXML
    private TextField locationTxt;
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
    private ComboBox<Contacts> contactIDCmbBx;
    @FXML
    private ComboBox<Customers> custIDCmbBx;
    @FXML
    private ComboBox<User> userIDCmbBx;

    Stage stage;
    Parent scene;

    /** This is a method that sends data to the correct screen.
     * This method gets called on the appointments main screen to populate the correct fields.
     * There are variables for each field declared for checking on the update method.
     * @param appointments Appointment Object
     * @throws SQLException SQL called */
    public void sendAppointment(Appointments appointments) throws SQLException
    {
        apptIDTxt.setText(String.valueOf(appointments.getAppointmentID()));
        titleTxt.setText(appointments.getTitle());
        descriptionTxt.setText(appointments.getDescription());
        locationTxt.setText(appointments.getLocation());
        typeTxt.setText(appointments.getType());
        startDatePicker.setValue(appointments.getStart().toLocalDate());
        startTimeCmbBx.setValue(appointments.getStart().toLocalTime());
        endDatePicker.setValue(appointments.getEnd().toLocalDate());
        endTimeCmbBx.setValue(appointments.getEnd().toLocalTime());

        for(Contacts contact : contactIDCmbBx.getItems())
        {
            if(appointments.getContactID() == contact.getContactID())
            {
                contactIDCmbBx.setValue(contact);
                break;
            }
        }

        for(Customers customer : custIDCmbBx.getItems())
        {
            if(appointments.getCustomerID() == customer.getCustomerID())
            {
                custIDCmbBx.setValue(customer);
                break;
            }
        }

        for(User user : userIDCmbBx.getItems())
        {
            if(appointments.getUserID() == user.getUserID())
            {
                userIDCmbBx.setValue(user);
                break;
            }
        }
    }

    /** This is an on action method.
     * There is a method called from the AppointmentDAO to update record in the database.
     * This will take the user back to the application main screen after success.
     *  There are a couple alerts to the user for valid data entry and operation business hours.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionUpdate(ActionEvent actionEvent) throws IOException
    {
        boolean overlapFlag = false;
        try {
            long appointmentID = Long.parseLong(apptIDTxt.getText());
            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            String type = typeTxt.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalTime startTime = startTimeCmbBx.getValue();
            LocalDate endDate = endDatePicker.getValue();
            LocalTime endTime = endTimeCmbBx.getValue();
            LocalDateTime start = LocalDateTime.of(startDate, startTime);
            LocalDateTime end = LocalDateTime.of(endDate, endTime);
            String lastUpdateBy = "script";
            long customerID = custIDCmbBx.getValue().getCustomerID();
            long userID = userIDCmbBx.getValue().getUserID();
            long contactID = contactIDCmbBx.getValue().getContactID();

            ZoneId myZoneId = ZoneId.systemDefault();

            ZonedDateTime myStartZDT = ZonedDateTime.of(start, myZoneId);
            ZonedDateTime myEndZDT = ZonedDateTime.of(end, myZoneId);

            ZoneId estZoneID = ZoneId.of("America/New_York");

            ZonedDateTime estStartZDT = ZonedDateTime.ofInstant(myStartZDT.toInstant(), estZoneID);
            ZonedDateTime estEndZTD = ZonedDateTime.ofInstant(myEndZDT.toInstant(), estZoneID);

            LocalTime businessStart = LocalTime.of(8, 0);
            LocalTime businessEnd = LocalTime.of(22, 0);

            if (estStartZDT.toLocalTime().isBefore(businessStart) ||
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
            else if (title.isBlank() || description.isBlank() || location.isBlank() || type.isBlank())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid entries for each field.");
                alert.setTitle("Error Editing Appointment");
                alert.showAndWait();
            }
            else
            {
                for (Appointments appts : AppointmentDAO.selectAll()) {
                    LocalDateTime checkingStart = appts.getStart();
                    LocalDateTime checkingEnd = appts.getEnd();
                    if(appts.getAppointmentID() == appointmentID){
                        continue;
                    }

                    if ((customerID == appts.getCustomerID()) &&
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
                if (!overlapFlag)
                {
                    int rowsAffected = AppointmentDAO.update(appointmentID, title, description, location, type, start, end, lastUpdateBy, customerID, userID, contactID);
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
            alert.setTitle("Error Editing Appointment");
            alert.showAndWait();
        }
    }

    /** This is an on action method.
     * This method will take the user back to the appointments main screen.
     * There is an alert for the user to confirm canceling the edit.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionCancel(ActionEvent actionEvent) throws IOException
    {
        Alert alert  = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to cancel editing this appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /** This is an override method.
     * This method gets called first for this screen.
     * This method calls several DAO classes to load data into the appropriate combo boxes.
     * @param resourceBundle for local objects.
     * @param url for global objects. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //this is where i set the combo boxes
        for (int i = 0; i < 24; i++)
        {
            startTimeCmbBx.getItems().add(LocalTime.of(i, 0));
            if (i < 23)
            {
                endTimeCmbBx.getItems().add(LocalTime.of(i + 1, 0));
            }
            else
            {
                endTimeCmbBx.getItems().add(LocalTime.of(0, 0));
            }
        }

        try
        {
            contactIDCmbBx.setItems(ContactsDAO.selectAll());
            custIDCmbBx.setItems(CustomerDAO.selectAll());
            userIDCmbBx.setItems(UserDAO.selectAll());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}