package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Customers;
import dao.AppointmentDAO;
import dao.CustomerDAO;
import dao.JDBC;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;

/** This class created the appointment main screen for the application. */
public class AppointmentsController implements Initializable {
    @FXML
    private RadioButton allRdBtn;
    @FXML
    private RadioButton byWeekRbd;
    @FXML
    private RadioButton byMonthRbd;
    @FXML
    private ToggleGroup sortAptsTG;

    @FXML
    private TableView<Appointments> aptTableView;
    @FXML
    private TableColumn<Appointments, Integer> aptIDCol;
    @FXML
    private TableColumn<Appointments, String> titleCol;
    @FXML
    private TableColumn<Appointments, String> descriptionCol;
    @FXML
    private TableColumn<Appointments, String> locationCol;
    @FXML
    private TableColumn<Appointments, String> typeCol;
    @FXML
    private TableColumn<Appointments, LocalDateTime> startCol;
    @FXML
    private TableColumn<Appointments, LocalDateTime> endCol;
    @FXML
    private TableColumn<Appointments, Integer> customerIDCol;
    @FXML
    private TableColumn<Appointments, Integer> userIDCol;
    @FXML
    private TableColumn<Appointments, Integer> contactIDCol;

    @FXML
    private TableView<Customers> custTableView;
    @FXML
    private TableColumn<Customers, Integer> custIDCol;
    @FXML
    private TableColumn<Customers, String> custNameCol;
    @FXML
    private TableColumn<Customers, String> postCodeCol;
    @FXML
    private TableColumn<Customers, String> phNumCol;
    @FXML
    private TableColumn<Customers, String> addressCol;
    @FXML
    private TableColumn<Customers, Integer> divisionIDCol;
    @FXML
    private TableColumn<Customers, Integer> countryIDCol;
    @FXML
    private Label timeZoneLbl;

    Stage stage;
    Parent scene;

    /**
     * An on action method that takes the user to the add appointment screen when a button is clicked.
     *
     * @param actionEvent Button Click
     * @throws IOException load
     */
    public void onActionAddApt(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * An on action method that takes the user to the edit appointment screen.
     * An appointment object must be selected on the appropriate table.
     * This calls a method from the edit screen to send the selected data over.
     * There are a couple alerts to the user for selecting appointments.
     *
     * @param actionEvent Button Click
     * @throws IOException  load
     * @throws SQLException SQL method
     */
    public void onActionEditApt(ActionEvent actionEvent) throws IOException, SQLException {
        Appointments appointment = aptTableView.getSelectionModel().getSelectedItem();

        if (AppointmentDAO.selectAll().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There are no appointments to edit.");
            alert.setTitle("Error Editing Appointment");
            alert.showAndWait();
        }
        if (appointment == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an appointment to edit.");
            alert.setTitle("Error Editing Appointment");
            alert.showAndWait();
        }
        if (appointment != null)
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/EditAppointment.fxml"));
            loader.load();

            EditAppointmentController EAController = loader.getController();
            EAController.sendAppointment(appointment);

            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } else {
            System.out.println("failed to get appointment to modify");
        }

    }

    /**
     * An on action method that will delete a selected appointment from the appropriate table.
     * This method will delete the appointment object from that table and the database.
     * There are are several alerts to the user to verify deletion.
     *
     * @param actionEvent Button Click
     * @throws SQLException SQL method
     */
    public void onActionDeleteApt(ActionEvent actionEvent) throws SQLException {
        Appointments appointment = aptTableView.getSelectionModel().getSelectedItem();

        if (AppointmentDAO.selectAll().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There are no appointments to delete.");
            alert.setTitle("Error Deleting Appointment");
            alert.showAndWait();
        }
        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select an appointment to delete.");
            alert.setTitle("Error Deleting Appointment");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to delete appointment with ID and type: " + appointment.getAppointmentID() + " " + appointment.getType() + " ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                int appointmentID = (int) appointment.getAppointmentID();
                int rowsAffected = AppointmentDAO.delete(appointmentID);
                if (rowsAffected > 0) {
                    aptTableView.setItems(AppointmentDAO.selectAll());
                } else {
                    System.out.println("Fail");
                }
            }
        }
    }

    /**
     * An on action method that takes the user to the add customer screen.
     *
     * @param actionEvent Button Click
     * @throws IOException load
     */
    public void onActionAddCust(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * An on action method that takes the user to the edit customer screen.
     * A customer object must be selected from the appropriate table.
     * This calls a method from the edit screen to send the selected data over.
     * There are several alerts to the user on selecting customer objects.
     *
     * @param actionEvent Button Click
     * @throws IOException  load
     * @throws SQLException SQL called
     */
    public void onActionEditCust(ActionEvent actionEvent) throws IOException, SQLException {
        Customers customer = custTableView.getSelectionModel().getSelectedItem();

        if (CustomerDAO.selectAll().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There are no customers to edit.");
            alert.setTitle("Error Editing Customer");
            alert.showAndWait();
        }
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select an customer to edit.");
            alert.setTitle("Error Editing Customer");
            alert.showAndWait();
        }
        if (customer != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/EditCustomer.fxml"));
            loader.load();

            EditCustomerController ECController = loader.getController();
            ECController.sendCust(customer);

            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * An on action method that deletes a customer.
     * The customer must be selected by the user on the appropriate table.
     * This method will also delete any associated appointments from that table and the database.
     * There are several alerts to the user on deleting customer objects.
     *
     * @param actionEvent Button Click
     * @throws SQLException SQL called
     */
    public void onActionDeleteCust(ActionEvent actionEvent) throws SQLException {
        Customers customer = custTableView.getSelectionModel().getSelectedItem();
        if (CustomerDAO.selectAll().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There are no customers to delete.");
            alert.setTitle("Error Deleting Customer");
            alert.showAndWait();
        }
        if (customer == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select an customer to delete.");
            alert.setTitle("Error Deleting Customer");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to delete customer?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK)
            {
                int customerID = (int) customer.getCustomerID();
                for (Appointments appointments : aptTableView.getItems())
                {
                    if (appointments.getCustomerID() == customer.getCustomerID())
                    {
                        int appointmentID = (int) appointments.getAppointmentID();
                        AppointmentDAO.delete(appointmentID);
                    }
                }
                CustomerDAO.delete(customerID);
                custTableView.setItems(CustomerDAO.selectAll());
                aptTableView.setItems(AppointmentDAO.selectAll());
            }
            else {
                System.out.println("Failure to delete customer and associated appointments.");
            }
        }
    }

    /**
     * An on action method that takes the user to the reports screen.
     *
     * @param actionEvent Button Click
     * @throws IOException load
     */
    public void onActionReports(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Reports.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * An on action method that will log the user out and terminate the program.
     *
     * @param actionEvent Button Click
     */
    public void onActionLogOut(ActionEvent actionEvent) {
        JDBC.closeConnection();
        System.exit(0);
    }

    /**
     * This is a method that gets called when the radio buttons are selected.
     * This method gets called for the sortAptsTG radio selections.
     *
     * @throws SQLException SQL method
     */
    public void setSortAptsTG() throws SQLException {
        if (byWeekRbd.isSelected()) {
            LocalDate today = LocalDate.now();
            ObservableList<Appointments> weekFilterAppts = FXCollections.observableArrayList();

            if (sortAptsTG.getSelectedToggle().equals(byWeekRbd)) {
                System.out.println("Week selected");
                for (Appointments appt : AppointmentDAO.selectAll()) {
                    LocalDate sDate = appt.getStart().toLocalDate();
                    if (sDate.isBefore(today.plusDays(7)) && sDate.isAfter(today.minusDays(1))) {
                        weekFilterAppts.add(appt);
                    }
                }
            }
            aptTableView.setItems(weekFilterAppts);
        }
        if (byMonthRbd.isSelected()) {
            LocalDate today = LocalDate.now();
            System.out.println("Today: " + today);
            ObservableList<Appointments> monthFilterAppts = FXCollections.observableArrayList();
            if (sortAptsTG.getSelectedToggle().equals(byMonthRbd)) {
                System.out.println("Month selected");
                for (Appointments appt : AppointmentDAO.selectAll()) {
                    LocalDate sDate = appt.getStart().toLocalDate();
                    if (sDate.isBefore(today.plusDays(30)) && sDate.isAfter(today.minusDays(1))) {
                        monthFilterAppts.add(appt);
                    }
                }
            }
            aptTableView.setItems(monthFilterAppts);
        }
    }

    /**
     * An on action method that is called when the By Weekly radio button is selected.
     * This method calls the setSortAptsTG method.
     *
     * @param actionEvent Radio Button Click
     * @throws SQLException SQL method
     */
    public void onActionByWeekRdoBtn(ActionEvent actionEvent) throws SQLException {
        if (sortAptsTG.getSelectedToggle().equals(byWeekRbd)) {
            setSortAptsTG();
        }
    }

    /**
     * An on action method that is called when the By Monthly radio button is selected.
     * This method calls the setSortAptsTG method.
     *
     * @param actionEvent Radio Button Click
     * @throws SQLException SQL method
     */
    public void onActionByMonthRdoBtn(ActionEvent actionEvent) throws SQLException {
        if (sortAptsTG.getSelectedToggle().equals(byMonthRbd)) {
            setSortAptsTG();
        }
    }

    /**
     * An on action method that is called when the By All Appointments radio button is selected.
     * This method calls the setSortAptsTG method.
     *
     * @param actionEvent Radio Button Click
     * @throws SQLException SQL method
     */
    public void onActionAllRdBtn(ActionEvent actionEvent) throws SQLException {
        aptTableView.setItems(AppointmentDAO.selectAll());
    }

    /**
     * An Override Method initialize.
     * This is the first method to get called when the screen begins running.
     * This method loads the data onto the tables.
     * There is an alert that informs the user there is an appointment coming up in 15 minutes.
     * The alert is a lambda to run the program more efficiently.
     *
     * @param resourceBundle for local dates and times.
     * @param url            for local dates and times.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //sets cell values and loads appointment table
        try {
            aptTableView.setItems(AppointmentDAO.selectAll());
            custTableView.setItems(CustomerDAO.selectAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        aptIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        contactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));

        //sets customers table cells and loads data from database
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        postCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phNumCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        divisionIDCol.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
        countryIDCol.setCellValueFactory(new PropertyValueFactory<>("countryID"));

        //this sets both buttons to false, so they are unmarked
        byMonthRbd.setSelected(false);
        byWeekRbd.setSelected(false);

        //this sets the appointment table to show all appointments
        allRdBtn.setSelected(true);

        //time zone label set
        timeZoneLbl.setText(String.valueOf(ZoneId.systemDefault()));

        try {
            LocalTime now = LocalTime.now();
            for (Appointments appt : AppointmentDAO.selectAll())
            {
                LocalTime sTime = appt.getStart().toLocalTime();
                if (sTime.isAfter(now) && sTime.isBefore(now.plusMinutes(15)))
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Upcoming Appointment");
                    alert.setContentText("There is an appointment upcoming in the next 15 minutes: " + appt.getAppointmentID() + " " + appt.getStart() + ".");

                    //alert lambda here
                    alert.showAndWait().ifPresent((response -> {
                        if (response == ButtonType.OK) {
                            System.out.println("Alerting!");
                            Parent main = null;
                        }
                    }
                    ));
                    break;
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There are no appointments upcoming in the next 15 minutes.");
                    alert.setTitle("No upcoming appointments");
                    alert.showAndWait();
                    break;
                }
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}